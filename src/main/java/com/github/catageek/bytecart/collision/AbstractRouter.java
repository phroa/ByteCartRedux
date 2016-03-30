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
package com.github.catageek.bytecart.collision;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collection.ExpirableMap;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.hardware.RegistryOutput;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.sign.Triggerable;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRouter extends AbstractCollisionAvoider implements Router {

    private static final ExpirableMap<Location<World>, Boolean> recentlyUsedMap = new ExpirableMap<>(40, false, "recentlyUsedRouter");
    private static final ExpirableMap<Location<World>, Boolean> hasTrainMap = new ExpirableMap<>(14, false, "hasTrainRouter");
    protected Map<Side, Side> fromTo = new ConcurrentHashMap<Side, Side>();
    protected Map<Side, Set<Side>> possibility = new ConcurrentHashMap<Side, Set<Side>>();
    private Direction from;
    private int secondpos = 0;
    private int posmask = 255;

    public AbstractRouter(Direction from, Location<World> loc) {
        super(loc);
        this.setFrom(from);
        this.addIO(from, loc.createSnapshot());

    }

    /**
     * Get the relative direction of an absolute direction with a specific origin
     *
     * @param from the origin axis
     * @param to the absolute direction
     * @return the relative direction
     */
    private final static Side getSide(Direction from, Direction to) {
        Direction t = to;
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
    private final static Direction turn(Direction b) {
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

    @Override
    public void Add(Triggerable t) {
        return;
    }

    @Override
    public final Direction wishToGo(Direction from, Direction to, boolean isTrain) {
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Router : coming from " + from + " going to " + to);
        }
        Router ca = this;
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
            // save router in collision avoider map
            ByteCartRedux.myPlugin.getCollisionAvoiderManager().setCollisionAvoider(this.getLocation(), ca);

            // activate secondary levers
            ca.getOutput(1).setAmount(ca.getSecondpos());

            //activate primary levers
            ca.route(from);
        }
        ca.book(isTrain);

        return ca.getTo();
    }

    @Override
    public void route(Direction from) {
        return;
    }

    @Override
    public abstract Direction getTo();

    @Override
    public final Direction getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    private final void setFrom(Direction from) {
        this.from = from;
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
    private final Side getSide(Direction to) {
        return getSide(getFrom(), to);
    }

    /**
     * Registers levers as output
     *
     * @param from the origin axis
     * @param center the center of the router
     */
    private final void addIO(Direction from, BlockSnapshot center) {

        Direction f = from;
        Direction g = MathUtil.clockwise(from);
        // Main output
        OutputPin[] sortie = new OutputPin[4];
        // East
        sortie[0] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.WEST.toVector3d().mul(3)).getRelative(Direction.SOUTH).createSnapshot());
        // North
        sortie[1] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.EAST.toVector3d().mul(3)).getRelative(Direction.NORTH).createSnapshot());
        // South
        sortie[3] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.SOUTH.toVector3d().mul(3)).getRelative(Direction.EAST).createSnapshot());
        // West
        sortie[2] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.NORTH.toVector3d().mul(3)).getRelative(Direction.WEST).createSnapshot());

        checkIOPresence(sortie);

        RegistryOutput main = new PinRegistry<OutputPin>(sortie);

        // output[0] is main levers
        this.addOutputRegistry(main);


        // Secondary output to make U-turn
        OutputPin[] secondary = new OutputPin[8];

        for (int i = 0; i < 7; i++) {
            // the first is Back
            secondary[i++] =
                    OutputPinFactory.getOutput(center.getLocation().get().add(f.toVector3d().mul(4)).add(g.toVector3d().mul(2)).createSnapshot());
            secondary[i] = OutputPinFactory.getOutput(center.getLocation().get().add(f.toVector3d().mul(6)).createSnapshot());
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

    @Override
    public final int getSecondpos() {
        return secondpos;
    }

    @Override
    public final void setSecondpos(int secondpos) {
        this.secondpos = secondpos;
    }

    @Override
    public final int getPosmask() {
        return posmask;
    }

    @Override
    public final void setPosmask(int posmask) {
        this.posmask = posmask;
    }

    @Override
    protected ExpirableMap<Location<World>, Boolean> getRecentlyUsedMap() {
        return recentlyUsedMap;
    }

    @Override
    protected ExpirableMap<Location<World>, Boolean> getHasTrainMap() {
        return hasTrainMap;
    }
}
