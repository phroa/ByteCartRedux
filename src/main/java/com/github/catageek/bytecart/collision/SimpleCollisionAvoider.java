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
import com.github.catageek.bytecart.hardware.RegistryOutput;
import com.github.catageek.bytecart.sign.Triggerable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A collision avoider for T cross-roads
 */
public class SimpleCollisionAvoider extends AbstractCollisionAvoider implements CollisionAvoider {

    private static final ExpirableMap<Location<World>, Boolean> recentlyUsedMap = new ExpirableMap<>(20, false, "recentlyUsed9000");
    private static final ExpirableMap<Location<World>, Boolean> hasTrainMap = new ExpirableMap<>(14, false, "hastrain");
    private final Location loc1;
    private RegistryOutput Lever1 = null, Lever2 = null, Active = null;
    private IntersectionSide.Side state;

    private boolean reversed;


    public SimpleCollisionAvoider(Triggerable ic, Location<World> loc) {
        super(loc);
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux: new IntersectionSide() at " + loc);
        }

        Lever1 = ic.getOutput(0);
        Active = Lever1;
        reversed = ic.isLeverReversed();
        loc1 = ic.getLocation();
        state = (Lever1.getValue() == 0 ? IntersectionSide.Side.LEVER_OFF : IntersectionSide.Side.LEVER_ON);
    }

    /**
     * Ask for a direction, requesting a possible transition
     *
     * @param s the direction where the cart goes to
     * @param isTrain true if it is a train
     * @return the direction actually taken
     */
    public IntersectionSide.Side wishToGo(IntersectionSide.Side s, boolean isTrain) {

        IntersectionSide.Side trueSide = getActiveTrueSide(s);

        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : WishToGo to side " + trueSide + " and isTrain is " + isTrain);
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : state is " + state);
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : recentlyUsed is " + this.getRecentlyUsed() + " and hasTrain is " + this.getHasTrain());
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Lever1 is " + Lever1.getValue());
        }

        if (trueSide != state
                && (Lever2 == null
                || (!this.getRecentlyUsed()) && !this.getHasTrain())) {
            Set(trueSide);
        }
        this.setRecentlyUsed(true);
        return state;

    }

    /**
     * Get the fixed side of the active lever.
     * the second IC lever can be reversed
     *
     * @param s the original side
     * @return the fixed side
     */
    private IntersectionSide.Side getActiveTrueSide(IntersectionSide.Side s) {
        if (Active != Lever2) {
            return s;
        }
        return getSecondLeverSide(s);
    }

    /**
     * Get the fixed side of the second lever
     * @param s the original side
     * @return the fixed side
     */
    private IntersectionSide.Side getSecondLeverSide(IntersectionSide.Side s) {
        return reversed ? s : s.opposite();
    }


    @Override
    public void Add(Triggerable t) {
        if (t.getLocation().equals(loc1)) {
            Active = Lever1;
            return;
        }
        if (Lever2 != null) {
            Active = Lever2;
            return;
        }
        Lever2 = t.getOutput(0);
        Active = Lever2;
        reversed ^= t.isLeverReversed();
        Lever2.setAmount(getSecondLeverSide(state).Value());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux: Add and setting lever2 to " + Lever2.getValue());
        }
    }

    /**
     * Activate levers. The 2 levers are in opposition
     *
     * @param s the side of the lever of the IC that created this collision avoider
     */
    private void Set(IntersectionSide.Side s) {
        this.Lever1.setAmount(s.Value());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux: Setting lever1 to " + Lever1.getValue());
        }
        if (this.Lever2 != null) {
            this.Lever2.setAmount(getSecondLeverSide(state).Value());
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux: Setting lever2 to " + Lever2.getValue());
            }
        }
        state = s;
    }

    @Override
    public int getSecondpos() {
        throw new UnsupportedOperationException();
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
