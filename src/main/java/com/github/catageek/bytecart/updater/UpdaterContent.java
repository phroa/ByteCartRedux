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
import com.github.catageek.bytecart.routing.Metric;
import com.github.catageek.bytecart.routing.RouteValue;
import com.github.catageek.bytecart.routing.RoutingTableWritable;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A class to store data in books used by updater
 */
public class UpdaterContent extends WandererContent implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 848098890652934583L;

    private boolean fullReset = false;
    private boolean isNew = false;
    private long lastRouterSeen;

    public UpdaterContent(CarriedInventory<?> inv, Wanderer.Level level, int region, Player player
            , boolean isfullreset) {
        this(inv, level, region, player, isfullreset, false);
    }

    public UpdaterContent(CarriedInventory<?> inv, Wanderer.Level level, int region, Player player
            , boolean isfullreset, boolean isNew) {
        super(inv, level, region, player);
        this.fullReset = isfullreset;
        this.isNew = isNew;
        this.setExpirationTime(ByteCartRedux.rootNode.getNode("updater", "timeout").getInt(60) * 60000 + getCreationTime());
    }

    /**
     * Get a set of the entries of the IGP packet
     *
     * @return the set
     */
    public Set<Entry<Integer, Metric>> getEntrySet() {
        return tableMap.entrySet();
    }

    /**
     * Build the IGP exchange packet
     *
     * @param table the routing table
     * @param direction the direction to exclude
     */
    void putRoutes(RoutingTableWritable table, DirectionRegistry direction) {
        tableMap.clear();
        Iterator<RouteValue> it = table.getOrderedRouteNumbers();
        while (it.hasNext()) {
            int i = it.next().value();
            if (table.getDirection(i) != null && table.getDirection(i).getAmount() != direction.getAmount()) {
                tableMap.put(i, new Metric(table.getMinMetric(i)));
                if (ByteCartRedux.debug) {
                    ByteCartRedux.log.info("ByteCartRedux : Route exchange : give ring " + i + " with metric " + table.getMinMetric(i) + " to " + table
                            .getDirection(i).getBlockFace());
                }
            }

        }
    }

    /**
     * Set the timestamp field to now
     */
    void seenTimestamp() {
        this.lastRouterSeen = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Get the time difference between now and the last time we called seenTimestamp()
     *
     * @return the time difference, or -1 if seenTimestamp() was never called
     */
    public int getInterfaceDelay() {
        if (lastRouterSeen != 0) {
            return (int) ((Calendar.getInstance().getTimeInMillis() - lastRouterSeen) / 1000);
        }
        return -1;
    }

    /**
     * @return the fullReset
     */
    boolean isFullReset() {
        return fullReset;
    }

    /**
     * @return the isNew
     */
    boolean isNew() {
        return isNew;
    }
}
