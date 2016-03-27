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
import com.github.catageek.bytecart.routing.BCCounter;
import com.github.catageek.bytecart.routing.Metric;
import com.github.catageek.bytecart.sign.BC8010;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.util.DirectionRegistry;
import com.github.catageek.bytecart.updater.Wanderer.Level;
import org.bukkit.block.BlockFace;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractRegionUpdater extends DefaultRouterWanderer {

    private final boolean IsTrackNumberProvider;
    protected UpdaterContent Routes;
    private BCCounter counter;

    AbstractRegionUpdater(BCSign bc, UpdaterContent rte) {
        super(bc, rte.getRegion());
        Routes = rte;
        counter = rte.getCounter();

        if (bc instanceof BC8010) {
            BC8010 ic = (BC8010) bc;
            IsTrackNumberProvider = ic.isTrackNumberProvider();
        } else {
            IsTrackNumberProvider = false;
        }
    }

    abstract protected void Update(BlockFace to);

    abstract protected int getTrackNumber();

    abstract protected BlockFace selectDirection();

    /**
     * Perform the IGP routing protocol update
     *
     * @param To the direction where we are going to
     */
    protected final void routeUpdates(BlockFace To) {
        if (isRouteConsumer()) {
            Set<Integer> connected = getRoutingTable().getDirectlyConnectedList(getFrom());
            int current = getCurrent();

            current = (current == -2 ? 0 : current);

            // if the track we come from is not recorded
            // or others track are wrongly recorded, we correct this
            if (current >= 0 && (!connected.contains(current) || connected.size() != 1)) {

                Iterator<Integer> it = connected.iterator();
                while (it.hasNext()) {
                    getRoutingTable().removeEntry(it.next(), getFrom());
                }

                // Storing the route from where we arrive
                if (ByteCartRedux.debug) {
                    ByteCartRedux.log.info("ByteCartRedux : Wanderer : storing ring " + current + " direction " + getFrom().ToString());
                }

                getRoutingTable().setEntry(current, getFrom(), new Metric(0));
                Routes.updateTimestamp();

            }

            // loading received routes in router if coming from another router
            if (this.getRoutes().getLastrouterid() != this.getCenter().hashCode()) {
                getRoutingTable().Update(getRoutes(), getFrom());
            }


            // preparing the routes to send
            Routes.putRoutes(getRoutingTable(), new DirectionRegistry(To));

            setCurrent(current);
            this.getRoutes().setLastrouterid(this.getCenter().hashCode());

            try {
                getRoutingTable().serialize();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doAction(BlockFace To) {

        this.Update(To);

        int current = getCurrent();
        current = (current == -2 ? 0 : current);
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : doAction() : current is " + current);
        }

        // If we are turning back, keep current track otherwise discard
        if (!isSameTrack(To)) {
            getRoutes().setCurrent(-1);
        }

        this.getRoutes().seenTimestamp();

        try {
            UpdaterContentFactory.saveContent(Routes);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public final BlockFace giveRouterDirection() {
        return this.selectDirection();
    }

    /**
     * Get the type of updater
     *
     * @return the type
     */
    public final Level getLevel() {
        return getRoutes().getLevel();
    }

    protected final UpdaterContent getRoutes() {
        return Routes;
    }

    protected final BCCounter getCounter() {
        return counter;
    }

    protected final int getCurrent() {
        if (getRoutes() != null)
        // current: track number we are on
        {
            return getRoutes().getCurrent();
        }
        return -1;
    }

    protected final void setCurrent(int current) {
        if (getRoutes() != null) {
            getRoutes().setCurrent(current);
        }
    }

    /**
     * @return true if the IC can receive routes
     */
    protected final boolean isRouteConsumer() {
        return getRoutes().getLevel().equals(this.getSignLevel());
    }

    /**
     * Clear the routing table, keeping ring 0
     */
    protected void reset() {
        boolean fullreset = this.getRoutes().isFullreset();
        if (fullreset) {
            this.getSignAddress().remove();
        }
        // clear routes except route to ring 0
        getRoutingTable().clear(fullreset);
        try {
            getRoutingTable().serialize();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Tells if this updater must provide track numbers for this IC
     *
     * @return true if this updater must provide track numbers
     */
    protected final boolean isTrackNumberProvider() {
        return IsTrackNumberProvider;
    }
}
