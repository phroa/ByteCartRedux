/**
 * ByteCart, ByteCart Redux
 * Copyright (C) Catageek
 * Copyright (C) phroa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.catageek.ByteCart.CollisionManagement;

import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.HAL.PinRegistry;
import com.github.catageek.ByteCart.HAL.RegistryOutput;
import com.github.catageek.ByteCart.IO.OutputPin;
import com.github.catageek.ByteCart.IO.OutputPinFactory;
import com.github.catageek.ByteCart.Signs.Triggable;
import com.github.catageek.ByteCart.Storage.ExpirableMap;
import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRouter extends AbstractCollisionAvoider implements Router {

    private static final ExpirableMap<Location, Boolean> recentlyUsedMap = new ExpirableMap<Location, Boolean>(40, false, "recentlyUsedRouter");
    private static final ExpirableMap<Location, Boolean> hasTrainMap = new ExpirableMap<Location, Boolean>(14, false, "hasTrainRouter");
    protected Map<Side, Side> FromTo = new ConcurrentHashMap<Side, Side>();
    protected Map<Side, Set<Side>> Possibility = new ConcurrentHashMap<Side, Set<Side>>();
    private BlockFace From;
    private int secondpos = 0;
    private int posmask = 255;

    public AbstractRouter(BlockFace from, org.bukkit.Location loc) {
        super(loc);
        this.setFrom(from);
        this.addIO(from, loc.getBlock());

    }

    /**
     * Get the relative direction of an absolute direction with a specific origin
     *
     * @param from the origin axis
     * @param to the absolute direction
     * @return the relative direction
     */
    private final static Side getSide(BlockFace from, BlockFace to) {
        BlockFace t = to;
        if (from == t) {
            return Side.BACK;
        }
        t = turn(t);
        if (from == t) {
            return Side.LEFT;
        }
        t = turn(t);
        if (from == t) {
            return Side.STRAIGHT;
        }
        return Side.RIGHT;
    }

    /**
     * Get the next absolute direction on the left
     *
     * @param b the initial direction
     * @return the next direction
     */
    private final static BlockFace turn(BlockFace b) {
        return MathUtil.anticlockwise(b);
    }

    /**
     * Bit-rotate a value on 8 bits
     *
     * @param value the value to rotate
     * @param d the numbers of bits to shift left
     * @return the result
     */
    private final static int leftRotate8(int value, int d) {
        int b = 8 - d;
        return (value >> (b)) | ((value & ((1 << b) - 1)) << d);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.CollisionAvoider#Add(com.github.catageek.ByteCartRedux.Signs.Triggable)
     */
    @Override
    public void Add(Triggable t) {
        return;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#WishToGo(org.bukkit.block.BlockFace, org.bukkit.block.BlockFace, boolean)
     */
    @Override
    public final BlockFace WishToGo(BlockFace from, BlockFace to, boolean isTrain) {
        //		IntersectionSide sfrom = getSide(from);
        //		IntersectionSide sto = getSide(to);


        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Router : coming from " + from + " going to " + to);
        }
/*		if(ByteCartRedux.debug)
            ByteCartRedux.log.info("ByteCartRedux : Router : going to " + sto);
*/
        Router ca = this;
/*
		if(ByteCartRedux.debug) {
			ByteCartRedux.log.info("ByteCartRedux : position found  " + ca.getClass().toString());
			ByteCartRedux.log.info("ByteCartRedux : Recently used ? " + recentlyUsed);
			ByteCartRedux.log.info("ByteCartRedux : hasTrain ? " + hasTrain );
			ByteCartRedux.log.info("ByteCartRedux : isTrain ? " + isTrain );
		}
*/
        Side s = getSide(from, to);

        boolean cond = !this.getRecentlyUsed() && !this.getHasTrain();

        if (this.getPosmask() != 255 || cond) {

            switch (s) {
                case STRAIGHT:
                    ca = new StraightRouter(from, getLocation());
                    if ((cond) || this.ValidatePosition(ca)) {
                        break;
                    }
                case RIGHT:
                    ca = new RightRouter(from, getLocation());
                    if ((cond) || this.ValidatePosition(ca)) {
                        break;
                    }
                case LEFT:
                    ca = new LeftRouter(from, getLocation());
                    if ((cond) || this.ValidatePosition(ca)) {
                        break;
                    }
                case BACK:
                    ca = new BackRouter(from, getLocation());
                    if ((cond) || this.ValidatePosition(ca)) {
                        break;
                    }
                default:
                    ca = new LeftRouter(from, getLocation());
                    if ((cond) || this.ValidatePosition(ca)) {
                        break;
                    }
                    ca = this;
            }
/*
			if(ByteCartRedux.debug)
				ByteCartRedux.log.info("ByteCartRedux : Router : position changed to " + ca.getClass().toString());
			if(ByteCartRedux.debug)
				ByteCartRedux.log.info("ByteCartRedux : Router : really going to " + ca.getTo());
*/            // save router in collision avoider map
            ByteCartRedux.myPlugin.getCollisionAvoiderManager().setCollisionAvoider(this.getLocation(), ca);

            // activate secondary levers
            ca.getOutput(1).setAmount(ca.getSecondpos());

            //activate primary levers
            ca.route(from);
        }
        ca.Book(isTrain);

        return ca.getTo();
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#route(org.bukkit.block.BlockFace)
     */
    @Override
    public void route(BlockFace from) {
        return;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#getTo()
     */
    @Override
    public abstract BlockFace getTo();

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#getFrom()
     */
    @Override
    public final BlockFace getFrom() {
        return From;
    }

    /**
     * @param from the from to set
     */
    private final void setFrom(BlockFace from) {
        From = from;
    }

    /**
     * Tell if a transition is necessary for the router to satisfy direction request
     *
     * @param ca the collision avoider against which to check the state
     * @return false if a transition is needed
     */
    private final boolean ValidatePosition(Router ca) {
        Side side = getSide(this.getFrom(), ca.getFrom());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : pos value befor rotation : " + Integer.toBinaryString(getSecondpos()));
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : rotation of bits         : " + side.Value());
        }
        int value = AbstractRouter.leftRotate8(getSecondpos(), side.Value());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : pos value after rotation : " + Integer.toBinaryString(value));
        }
        int mask = AbstractRouter.leftRotate8(getPosmask(), side.Value());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : mask after rotation      : " + Integer.toBinaryString(mask));
        }
        ca.setSecondpos(value | ca.getSecondpos());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : value after OR           : " + Integer.toBinaryString(ca.getSecondpos()));
        }
        ca.setPosmask(mask | ca.getPosmask());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : mask after OR            : " + Integer.toBinaryString(ca.getPosmask()));
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : compatible ?             : " + (((value ^ ca.getSecondpos()) & mask) == 0));
        }
        return ((value ^ ca.getSecondpos()) & mask) == 0;
    }

    /**
     * Get the relative direction of an absolute direction
     *
     * @param to the absolute direction
     * @return the relative direction
     */
    @SuppressWarnings("unused")
    private final Side getSide(BlockFace to) {
        return getSide(getFrom(), to);
    }

    /**
     * Registers levers as output
     *
     * @param from the origin axis
     * @param center the center of the router
     */
    private final void addIO(BlockFace from, org.bukkit.block.Block center) {

        BlockFace f = from;
        BlockFace g = MathUtil.clockwise(from);
        // Main output
        OutputPin[] sortie = new OutputPin[4];
        // East
        sortie[0] = OutputPinFactory.getOutput(center.getRelative(BlockFace.WEST, 3).getRelative(BlockFace.SOUTH));
        // North
        sortie[1] = OutputPinFactory.getOutput(center.getRelative(BlockFace.EAST, 3).getRelative(BlockFace.NORTH));
        // South
        sortie[3] = OutputPinFactory.getOutput(center.getRelative(BlockFace.SOUTH, 3).getRelative(BlockFace.EAST));
        // West
        sortie[2] = OutputPinFactory.getOutput(center.getRelative(BlockFace.NORTH, 3).getRelative(BlockFace.WEST));

        checkIOPresence(sortie);

        RegistryOutput main = new PinRegistry<OutputPin>(sortie);

        // output[0] is main levers
        this.addOutputRegistry(main);


        // Secondary output to make U-turn
        OutputPin[] secondary = new OutputPin[8];

        for (int i = 0; i < 7; i++) {
            // the first is Back
            secondary[i++] = OutputPinFactory.getOutput(center.getRelative(f, 4).getRelative(g, 2));
            secondary[i] = OutputPinFactory.getOutput(center.getRelative(f, 6));
            f = g;
            g = MathUtil.clockwise(g);
        }

        checkIOPresence(secondary);

        RegistryOutput second = new PinRegistry<OutputPin>(secondary);

        // output[1] is second and third levers
        this.addOutputRegistry(second);

    }

    /**
     * Check if there are levers as expected
     *
     * @param sortie an array of levers
     */
    private void checkIOPresence(OutputPin[] sortie) {
        for (int i = 0; i < sortie.length; i++) {
            if (sortie[i] == null) {
                ByteCartRedux.log.error("ByteCartRedux : Lever missing or wrongly positioned in router " + this.getLocation());
                throw new NullPointerException();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#getSecondpos()
     */
    @Override
    public final int getSecondpos() {
        return secondpos;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#setSecondpos(int)
     */
    @Override
    public final void setSecondpos(int secondpos) {
        this.secondpos = secondpos;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#getPosmask()
     */
    @Override
    public final int getPosmask() {
        return posmask;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.Router#setPosmask(int)
     */
    @Override
    public final void setPosmask(int posmask) {
        this.posmask = posmask;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.AbstractCollisionAvoider#getRecentlyUsedMap()
     */
    @Override
    protected ExpirableMap<Location, Boolean> getRecentlyUsedMap() {
        return recentlyUsedMap;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.CollisionManagement.AbstractCollisionAvoider#getHasTrainMap()
     */
    @Override
    protected ExpirableMap<Location, Boolean> getHasTrainMap() {
        return hasTrainMap;
    }
}
