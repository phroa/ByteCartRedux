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

import com.github.catageek.bytecart.ByteCartAPI;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.sign.BCRouter;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.sign.HasNetmask;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.util.Direction;

import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class DefaultLocalWanderer<T extends InventoryContent> extends AbstractWanderer {

    private final int SignNetmask;
    private final Stack<Integer> Start;
    private final Stack<Integer> End;
    private final T content;
    private final Counter Counter;
    protected com.github.catageek.bytecart.routing.RoutingTable RoutingTable = null;

    protected DefaultLocalWanderer(BCSign bc, T rte) {
        super(bc, rte.getRegion());
        content = rte;
        Counter = content.getCounter();

        Start = content.getStart();
        End = content.getEnd();

        if (bc instanceof HasNetmask) {
            SignNetmask = ((HasNetmask) bc).getNetmask();
        } else {
            SignNetmask = 0;
        }

        if (bc instanceof BCRouter) {
            BCRouter ic = (BCRouter) bc;
            RoutingTable = ic.getRoutingTable();
        }

        // set cookie A to wait a router
        if (this.getContent().getCurrent() == -2) {
            this.getStart().push(1);
            this.getContent().setCurrent(0);
            save();
        }


    }

    @Override
    public Side giveSimpleDirection() {


        // turn if it's not a station, and the ring is initialized or the address is invalid
        // and the subnet is contained in the current borders
        // and we are in the region
        if (this.getNetmask() < 8
                && (this.getStart().empty() == this.getEnd().empty())
                && !this.isExactSubnet(this.getFirstStationNumber(), this.getNetmask())
                && this.getWandererRegion() == this.getCounter().getCount(counterSlot.REGION.slot)) {
            return Side.LEVER_ON;
        }

        return Side.LEVER_OFF;
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

        // there is a cookie (so it is cookie A) or it's a reset cart
        if (this.getStart().empty() ^ this.getEnd().empty()) {
            int signring = this.getSignAddress().getTrack().getValue();
            int preferredroute = this.getStart().peek();

            // if we are not arrived yet or in ring 0, we continue
            if (signring == 0 || signring != preferredroute) {
                try {
                    return RoutingTable.getDirection(preferredroute).getBlockFace();
                } catch (NullPointerException e) {
                    // no route to ring
                    return getRandomBlockFace(RoutingTable, getFrom().getBlockFace());
                }
            }
        } else {
            // no cookie
            // check counter
            if (this.getCounter().isAllFull(0, 15)) {
                // we configured all stations
                // incrementing ring counter in the RoutingTableExchange map
                int ring = this.getCounter().getCount(counterSlot.RING.slot);
                incrementRingCounter(ring);

                // reset counters
                this.getCounter().resetAll();
                //clear stacks and set cookie B
                this.getStart().clear();
                this.getEnd().clear();
                this.getStart().push(0);

                save();

                // the route where we went the lesser
                int preferredroute = this.getContent().getMinDistanceRing(RoutingTable, getFrom());
                DirectionRegistry dir;
                if ((dir = RoutingTable.getDirection(preferredroute)) != null) {
                    return dir.getBlockFace();
                }
                return getRandomBlockFace(RoutingTable, getFrom().getBlockFace());
            }
        }
        return this.getFrom().getBlockFace();
    }

    @Override
    public void doAction(Direction to) {

        int signring = this.getSignAddress().getTrack().getValue();
        // the route where we went the lesser
        int preferredroute = this.getContent().getMinDistanceRing(RoutingTable, this.getFrom());


        // if we are not in the good region or on ring 0, skip update
        if (this.getSignAddress().getRegion().getValue() != getContent().getRegion()
                || signring == 0) {
            return;
        }

        // if this is cookie A, do nothing if this is not the route where we want to go
        if (this.getEnd().empty() ^ this.getStart().empty()
                && this.getStart().peek() != 0) {
            preferredroute = this.getStart().peek();
            if (preferredroute != signring) {
                return;
            }
        }

        // mark all stations of subnets in stack as taken
        while (!this.getEnd().empty()) {
            this.leaveSubnet();
        }

        //if cookie B is present, leave cookie A and return
        if (this.getEnd().empty() ^ this.getStart().empty()) {
            if (this.getStart().pop() == 0) {
                // Pushing the route where we want to go
                this.getStart().push(preferredroute);
                save();
                return;
            }
        }

        // leave subnet, resetting start and end stacks (and removing cookie A)
        this.getStart().clear();
        this.getEnd().clear();

        // updating region and counter data from sign
        this.getCounter().setCount(counterSlot.REGION.slot, this.getSignAddress().getRegion().getValue());
        this.getCounter().setCount(counterSlot.RING.slot, this.getSignAddress().getTrack().getValue());

        // mark station 0 as taken
        this.getCounter().incrementCount(0, 32);

        save();

    }

    @Override
    public void doAction(Side to) {
        // cookie still there
        if (this.getStart().empty() ^ this.getEnd().empty()) {
            return;
        }

        // we did not enter the subnet
        if (to.Value() != Side.LEVER_ON.Value() && this.getNetmask() < 8) {
            // if we have the same sign as when entering the subnet, close the subnet
            if (this.isExactSubnet(this.getFirstStationNumber(), this.getNetmask())) {
                this.leaveSubnet();
                this.save();
            }
            return;
        }

        int length = (256 >> this.getNetmask());

        int stationfield = -1;
        if (getSignAddress().isValid()) {
            stationfield = this.getSignAddress().getStation().getValue();
        }

        if (length != 1) {
            if (stationfield != -1) {
                // register new subnet start and mask
                Stack<Integer> startstack = this.getStart();
                Stack<Integer> endstack = this.getEnd();
                startstack.push(stationfield);
                endstack.push(stationfield + length);
            }
        } else
            // case of stations
            if (stationfield != -1) {
                this.getCounter().incrementCount(stationfield, 64);
            }

        save();
    }

    protected final int getNetmask() {
        return SignNetmask;
    }

    protected final Stack<Integer> getStart() {
        return Start;
    }

    protected final Stack<Integer> getEnd() {
        return End;
    }

    protected final Counter getCounter() {
        return Counter;
    }

    protected final T getContent() {
        return content;
    }

    /**
     * Get the current element of the first station number stack
     *
     *
     * @return the first station number
     */
    protected int getFirstStationNumber() {
        return (this.getStart().empty() ? 0 : this.getStart().peek());
    }

    protected final boolean isExactSubnet(int address, int netmask) {
        return (address == this.getFirstStationNumber() && (address | (255 >> netmask)) == (this.getLastStationNumber() - 1));
    }

    /**
     * Get the current element of the last station number stack
     *
     *
     * @return the last station number
     */
    protected int getLastStationNumber() {
        return (this.getEnd().empty()) ? 256 : this.getEnd().peek();
    }

    protected void incrementRingCounter(int ring) {
        if (this.getContent().hasRouteTo(ring)) {
            this.getContent().setRoute(ring
                    , this.getContent().getMetric(ring) + (new Random()).nextInt(RoutingTable.size()) + 1);
        } else {
            this.getContent().setRoute(ring, (new Random()).nextInt(RoutingTable.size()) + 1);
        }
    }

    public void save() {
        try {
            ByteCartAPI.getPlugin().getWandererManager().saveContent(getContent());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void leaveSubnet() {
        if (!this.getStart().empty() && !this.getEnd().empty()) {
            this.fillSubnet();
        }
    }

    private final void fillSubnet() {
        int start = this.getFirstStationNumber();
        int end = this.getLastStationNumber();
        for (int i = start; i < end; i++) {
            this.getCounter().incrementCount(i, 64);
        }
    }

    protected enum counterSlot {
        REGION(16),
        RING(17);

        public final int slot;

        counterSlot(int i) {
            this.slot = i;
        }
    }

}
