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
package com.github.catageek.ByteCart.Updaters;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.Event.UpdaterSetRingEvent;
import com.github.catageek.ByteCart.Signs.BCSign;
import com.github.catageek.ByteCart.Util.LogUtil;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import com.github.catageek.ByteCart.Wanderer.WandererContentFactory;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;

import java.util.Random;

class UpdaterRegion extends AbstractRegionUpdater implements Wanderer {

    UpdaterRegion(BCSign bc, UpdaterContent rte) {
        super(bc, rte);
    }

    private final String getAddress(int ring) {
        return "" + getWandererRegion() + "." + ring + ".0";
    }

    private int setSign(int current) {
        current = findFreeTrackNumber(current);
        // update sign with new number we found or current
        this.getSignAddress().setAddress(this.getAddress(current));
        this.getSignAddress().finalizeAddress();
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : change address on sign to " + this.getAddress(current));
        }
        return current;
    }

    private int findFreeTrackNumber(int current) {
        if (current == -2) {
            return 0;
        }
        while (current < 0) {
            // find a free number for the track if needed
            current = this.getCounter().firstEmpty();
            // if there is already a route, find another number
            // except if the route connects to here
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : trying ring " + current);
            }
            if (!this.getRoutingTable().isEmpty(current)
                    && !this.getRoutingTable().isDirectlyConnected(current, getFrom())) {
                this.getCounter().incrementCount(current);
                current = -1;
            }
        }
        return current;
    }


    private int getOrSetCurrent(int current) {
        // check if the sign has not priority
        if (current > 0 && current < getTrackNumber()) {
            // current < sign => reset counter, clear route and write sign
            this.getCounter().reset(getTrackNumber());
            this.getRoutingTable().removeEntry(getTrackNumber(), getFrom());
            this.getSignAddress().setAddress(this.getAddress(current));
            this.getSignAddress().finalizeAddress();
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : change address on sign to " + this.getAddress(current));
            }
            return current;
        } else {
            // sign seems to have priority
            // if the router knows that it is directly connected
            // we keep it, otherwise we find a new number (if possible)
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : getOrSetCurrent() : current as track on sign " + this.getTrackNumber());
            }

            return getTrackNumber();
        }
    }

    private boolean isSignNeedUpdate(int current) {
        int track = getTrackNumber();

        if (!this.getRoutes().isNew() && track == -1 && current == -2) {
            String error = "First BC8010 sign met has no address. If it is an initial configuration" +
                    ", add option 'new' at the end of bcupdater command to confirm." +
                    " If this is not a new network (i.e. you have already used bcupdater)" +
                    ", you should start from anywhere but here";
            LogUtil.sendError(this.getRoutes().getPlayer(), error);
            WandererContentFactory.deleteContent(this.getRoutes().getInventory());
            return true;
        }
        return (track == -1 && current != -2)
                || (track != -1 && current != -2 && getCounter().getCount(track) == 0)
                || (current >= 0 && current != track)
                || (track > 0 && !this.getRoutingTable().isDirectlyConnected(track, getFrom()));
    }

    protected BlockFace selectDirection() {
        BlockFace face;
        if ((face = manageBorder()) != null) {
            return face;
        }

        if (this.getRoutes() != null) {
            // current: track number we are on
            int current = this.getRoutes().getCurrent();

            if (this.isSignNeedUpdate(current)) {
                if (ByteCartRedux.debug) {
                    ByteCartRedux.log.info("ByteCartRedux : selectDirection() : sign need update as current = " + current);
                }
                return this.getFrom().getBlockFace();
            }

            // if there is a side we don't have visited yet, let go there
            if (isTrackNumberProvider()) {
                try {
                    if ((face = this.getRoutingTable().getFirstUnknown()) != null && !this.isSameTrack(face)) {
                        if (ByteCartRedux.debug) {
                            ByteCartRedux.log.info("ByteCartRedux : selectDirection() : first unknown " + face.toString());
                        }
                        return face;
                    }
                } catch (NullPointerException e) {
                    LogUtil.sendError(this.getRoutes().getPlayer(),
                            "ByteCartRedux : Chest expected at position " + this.getCenter().getRelative(BlockFace.UP, 5).getLocation());
                    throw e;
                }

                int min;
                if ((min = this.getCounter().getMinimum(this.getRoutingTable(), this.getFrom())) != -1) {
                    if (ByteCartRedux.debug) {
                        ByteCartRedux.log.info("ByteCartRedux : selectDirection() : minimum counter " + min);
                    }
                    return this.getRoutingTable().getDirection(min).getBlockFace();
                }
            }
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : selectDirection() : default ");
        }
        return DefaultRouterWanderer.getRandomBlockFace(this.getRoutingTable(), this.getFrom().getBlockFace());
    }

    @Override
    public void Update(BlockFace To) {

        // current: track number we are on
        int current = getCurrent();
        boolean isNew = (current < 0);

        if (getRoutes() != null) {

            UpdaterSetRingEvent event = null;

            if (isTrackNumberProvider() && !getSignAddress().isValid()) {
                // if there is no address on the sign
                // we provide one
                current = setSign(current);
                event = new UpdaterSetRingEvent(this, -1, current);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }

            if (getSignAddress().isValid()) {
                // there is an address on the sign
                int old = getTrackNumber();
                current = getOrSetCurrent(current);
                event = new UpdaterSetRingEvent(this, old, current);
                if (old != current) {
                    Bukkit.getServer().getPluginManager().callEvent(event);
                }
            }


            setCurrent(current);
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : Update() : current is " + current);
            }

            // update track counter if we have entered a new one
            if (current > 0 && isNew) {
                this.getCounter().incrementCount(current, new Random().nextInt(this.getRoutingTable().size() + 1) + 1);
            }

            routeUpdates(To);
        }
    }


    @Override
    public final int getTrackNumber() {
        Address address;
        if ((address = getSignAddress()).isValid()) {
            return address.getTrack().getAmount();
        }
        return -1;
    }
}
