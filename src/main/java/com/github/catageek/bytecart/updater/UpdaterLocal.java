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
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.event.custom.UpdaterEnterSubnetEvent;
import com.github.catageek.bytecart.event.custom.UpdaterLeaveSubnetEvent;
import com.github.catageek.bytecart.event.custom.UpdaterPassStationEvent;
import com.github.catageek.bytecart.event.custom.UpdaterSetStationEvent;
import com.github.catageek.bytecart.event.custom.UpdaterSetSubnetEvent;
import com.github.catageek.bytecart.io.ComponentSign;
import com.github.catageek.bytecart.sign.BC9001;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.util.LogUtil;
import org.spongepowered.api.Sponge;

import java.util.Stack;

public class UpdaterLocal extends DefaultLocalWanderer<UpdaterContent> implements Wanderer {

    UpdaterLocal(BCSign bc, UpdaterContent rte) {
        super(bc, rte);
    }

    @Override
    public void doAction(Side to) {

        if (this.getNetmask() == 8) {
            // Erase default name "Station"
            // TODO : added 04/2015, to be removed
            if (((BC9001) this.getBcSign()).getStationName().equals("Station")) {
                (new ComponentSign(this.getCenter())).setLine(2, "");
            }

            //it's a station, launch event
            UpdaterPassStationEvent event = new UpdaterPassStationEvent(this, this.getSignAddress(), ((BC9001) this.getBcSign()).getStationName());
            Sponge.getEventManager().post(event);
        }

        // cookie still there
        if (this.getStart().empty() ^ this.getEnd().empty()) {
            return;
        }

        // we did not enter the subnet
        int start;
        if (to.Value() != Side.LEVER_ON.Value() && this.getNetmask() < 8) {
            // if we have the same sign as when entering the subnet, close the subnet
            if (this.isExactSubnet((start = this.getFirstStationNumber()), this.getNetmask())) {
                this.getSignAddress().setAddress(buildAddress(start));
                this.getSignAddress().finalizeAddress();
                this.getContent().updateTimestamp();
                this.leaveSubnet();
                this.save();
            }
            return;
        }

        int length = (256 >> this.getNetmask());
        // if sign is not consistent, rewrite it
        if (!getSignAddress().isValid() || this.needUpdate()) {
            Address old = this.getSignAddress();
            if ((start = this.getFreeSubnet(getNetmask())) != -1) {
                String address = buildAddress(start);
                this.getSignAddress().setAddress(address);
                this.getSignAddress().finalizeAddress();

                // reload sign
                Address reloadAddress = AddressFactory.getAddress(address);
                this.setSignAddress(reloadAddress);

                this.getContent().updateTimestamp();

                // launch event
                if (length > 1) {
                    UpdaterSetSubnetEvent event = new UpdaterSetSubnetEvent(this, old, reloadAddress, length);
                    Sponge.getEventManager().post(event);
                } else {
                    UpdaterSetStationEvent event = new UpdaterSetStationEvent(this, old, reloadAddress, ((BC9001) this.getBcSign()).getStationName());
                    Sponge.getEventManager().post(event);
                }

                if (ByteCartRedux.debug) {
                    ByteCartRedux.log
                            .info("ByteCartRedux : UpdaterLocal : Update() : rewrite sign to " + address + "(" + this.getSignAddress().toString()
                                    + ")");
                }
            }
        }

        int stationfield = -1;
        if (getSignAddress().isValid()) {
            stationfield = this.getSignAddress().getStation().getValue();
        }

        if (length != 1) {
            // general case
            // if we go out from the current subnet and possibly entering a new one
            if (!this.isInSubnet(stationfield, this.getNetmask())) {
                leaveSubnet();
            }

            if (stationfield != -1) {
                // register new subnet start and mask
                Stack<Integer> startstack = this.getStart();
                Stack<Integer> endstack = this.getEnd();
                int oldstart = getFirstStationNumber();
                int oldend = getLastStationNumber();
                startstack.push(stationfield);
                endstack.push(stationfield + length);
                // launch event
                UpdaterEnterSubnetEvent event = new UpdaterEnterSubnetEvent(this, getSignAddress(), length,
                        AddressFactory.getAddress(buildAddress(oldstart)), oldend - oldstart);
                Sponge.getEventManager().post(event);
            }
        } else
            // case of stations
            if (stationfield != -1) {
                this.getCounter().incrementCount(stationfield, 64);
            }

        save();
    }

    public final void leaveSubnet() {
        super.leaveSubnet();
        if (!this.getStart().empty() && !this.getEnd().empty()) {
            Stack<Integer> startstack = this.getStart();
            Stack<Integer> endstack = this.getEnd();
            int start = startstack.pop();
            int end = endstack.pop();
            int newstart = getFirstStationNumber();
            int newend = getLastStationNumber();
            // launch event
            UpdaterLeaveSubnetEvent event = new UpdaterLeaveSubnetEvent(this, AddressFactory.getAddress(buildAddress(start)), end - start
                    , AddressFactory.getAddress(buildAddress(newstart)), newend - newstart);
            Sponge.getEventManager().post(event);
        }
    }

    private int getFreeSubnet(int netmask) {
        boolean free;
        int start = getFirstStationNumber();
        int end = getLastStationNumber();
        int step = 256 >> netmask;
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : getFreeSubnet() : start = "
                    + start + " end " + end + " step = " + step + "\n" + this.getCounter().toString());
        }
        for (int i = start; i < end; i += step) {
            free = true;
            for (int j = i; j < i + step; j++) {
                free &= (this.getCounter().getCount(j) == 0);
            }
            if (free) {
                if (ByteCartRedux.debug) {
                    ByteCartRedux.log.info("ByteCartRedux : getFreeSubnet() : testing : " + i + " : " + free);
                }
                return i;
            }
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : getFreeSubnet() : testing : " + i + " : " + free);
            }
        }
        LogUtil.sendError(this.getContent().getPlayer(), "Sign at " + this.getCenter().getLocation().toString()
                + "could not get an address because address pool is empty." +
                " Maximum station numbers is reached on the " + step + "-station subnet " + this.buildAddress(start));
        return -1;
    }

    private String buildAddress(int start) {
        return "" + this.getCounter().getCount(counterSlot.REGION.slot)
                + "." + getCurrent()
                + "." + start;
    }


    private boolean isInSubnet(int address, int netmask) {
        return (address >= this.getFirstStationNumber() && (address | (255 >> netmask)) < this.getLastStationNumber());
    }

    private boolean needUpdate() {
        return getSignAddress().getRegion().getValue() != this.getCounter().getCount(counterSlot.REGION.slot)
                || getSignAddress().getTrack().getValue() != this.getCounter().getCount(counterSlot.RING.slot)
                || !isInSubnet(getSignAddress().getStation().getValue(), this.getNetmask());
    }


    private int getCurrent() {
        return this.getCounter().getCount(counterSlot.RING.slot);
    }

    @Override
    public final Level getLevel() {
        return Level.LOCAL;
    }

    @Override
    public int getTrackNumber() {
        Address address;
        if ((address = this.getSignAddress()).isValid()) {
            return address.getTrack().getValue();
        }
        return -1;
    }
}
