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
package com.github.catageek.bytecart.updater;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.collision.IntersectionSide;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.event.custom.UpdaterClearStationEvent;
import com.github.catageek.bytecart.event.custom.UpdaterClearSubnetEvent;
import com.github.catageek.bytecart.event.custom.UpdaterSignInvalidateEvent;
import com.github.catageek.bytecart.sign.BC9001;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Direction;

import java.util.Stack;

final class UpdaterResetLocal extends UpdaterLocal implements Wanderer {

    UpdaterResetLocal(BCSign bc, UpdaterContent rte) {
        super(bc, rte);
    }

    @Override
    public void doAction(Direction to) {
        int ring;
        if ((ring = this.getTrackNumber()) != -1) {
            incrementRingCounter(ring);
        }
        this.getEnd().clear();
        // save the region number
        this.getContent().setCurrent(this.getSignAddress().getRegion().getValue());
        save();
    }

    @Override
    public void doAction(Side to) {
        Address address = this.getSignAddress();

        // Keep track on the subring level we are in
        int mask = this.getNetmask();
        if (mask < 8) {
            Stack<Integer> end = this.getEnd();
            if (to.equals(Side.LEVER_ON) && (end.isEmpty() || mask > end.peek())) {
                end.push(mask);
                if (ByteCartRedux.debug) {
                    ByteCartRedux.myPlugin.getLog().info("pushing mask " + mask + " on stack");
                }
            } else if (to.equals(Side.LEVER_OFF) && !end.isEmpty()) {
                if (ByteCartRedux.debug) {
                    ByteCartRedux.myPlugin.getLog().info("popping mask " + end.peek() + " from stack");
                }

                end.pop();
            }
        }
        save();

        // if we are not in the good region, skip update
        if (getContent().getCurrent() != getContent().getRegion()) {
            return;
        }

        if (address.isValid()) {
            if (this.getContent().isFullReset()) {
                if (this.getNetmask() == 8) {
                    UpdaterClearStationEvent event = new UpdaterClearStationEvent(this, address, ((BC9001) this.getBcSign()).getStationName());
                    Sponge.getEventManager().post(event);
                } else {
                    UpdaterClearSubnetEvent event = new UpdaterClearSubnetEvent(this, address, 256 >> this.getNetmask());
                    Sponge.getEventManager().post(event);
                }
                address.remove();
                if (ByteCartRedux.debug) {
                    ByteCartRedux.myPlugin.getLog().info("removing address");
                }
            }
        } else {
            UpdaterSignInvalidateEvent event = new UpdaterSignInvalidateEvent(this);
            Sponge.getEventManager().post(event);
            address.remove();
            if (ByteCartRedux.debug) {
                ByteCartRedux.myPlugin.getLog().info("removing invalid address");
            }
        }
    }

    @Override
    public IntersectionSide.Side giveSimpleDirection() {
        int mask = this.getNetmask();
        Stack<Integer> end = this.getEnd();
        if (mask < 8 && (end.isEmpty() || mask > end.peek())) {
            return IntersectionSide.Side.LEVER_ON;
        }
        return IntersectionSide.Side.LEVER_OFF;
    }

    @Override
    public Direction giveRouterDirection() {
        // check if we are in the good region
        if (this.getSignAddress().isValid()
                && this.getSignAddress().getRegion().getValue() != getWandererRegion()) {
            // case this is not the right region
            DirectionRegistry dir = RoutingTable.getDirection(getWandererRegion());
            if (dir != null) {
                return dir.getBlockFace();
            }
            return this.getFrom().getBlockFace();
        }
        // the route where we went the lesser
        int preferredroute = this.getContent().getMinDistanceRing(RoutingTable, getFrom());
        DirectionRegistry dir;
        if ((dir = RoutingTable.getDirection(preferredroute)) != null) {
            return dir.getBlockFace();
        }
        return DefaultRouterWanderer.getRandomBlockFace(RoutingTable, getFrom().getBlockFace());
    }
}
