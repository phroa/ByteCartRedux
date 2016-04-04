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
import com.github.catageek.bytecart.routing.RouteValue;
import com.github.catageek.bytecart.routing.RoutingTable;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class WandererContent implements InventoryContent {

    /**
     *
     */
    private static final long serialVersionUID = -9068486630910859194L;
    final Map<Integer, Metric> tableMap = new HashMap<>();
    private transient CarriedInventory<?> inventory = null;
    private final String player;
    private BCCounter counter;
    private long creationTime = Calendar.getInstance().getTimeInMillis();
    private int lastRouterId;
    private Stack<Integer> start;
    private Stack<Integer> end;
    //internal variable used by updaters
    private int current = -2;
    private long expirationTime;
    private Wanderer.Level level;
    private int region;

    WandererContent(CarriedInventory<?> inv, Wanderer.Level level, int region, Player player) {
        this.region = region;
        this.level = level;
        this.inventory = inv;
        this.player = player.getName();
        counter = new BCCounter();
        setStart(new Stack<>());
        setEnd(new Stack<>());
    }

    /**
     * Get the level of the updater
     *
     * @return the level
     */
    public Wanderer.Level getLevel() {
        return level;
    }

    /**
     * Set the level of the updater
     *
     * @param level the level to store
     */
    final void setLevel(Wanderer.Level level) {
        this.level = level;
    }

    /**
     * Get the region of the updater
     *
     * @return the region
     */
    public int getRegion() {
        return region;
    }

    /**
     * Set the region of the updater
     *
     * @param region the region to set
     */
    final void setRegion(int region) {
        this.region = region;
    }

    /**
     * Get the ring id where the updater thinks it is in
     *
     * @return the ring id
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Set the ring id where the updater thinks it is in
     *
     * @param current the ring id
     */
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * @return the counter
     */
    public BCCounter getCounter() {
        return counter;
    }

    /**
     * Set the counter instance
     *
     * @param counter the counter instance to set
     */
    final void setCounter(BCCounter counter) {
        this.counter = counter;
    }

    /**
     * @return the inventory
     */
    public CarriedInventory<?> getInventory() {
        return inventory;
    }

    /**
     * @param inventory the inventory to set
     */
    public void setInventory(CarriedInventory<?> inventory) {
        this.inventory = inventory;
    }

    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationtime to set
     */
    @SuppressWarnings("unused")
    private void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return Sponge.getServer().getPlayer(player).get();
    }


    /**
     * Get the id previously stored
     *
     * @return the id
     */
    public final int getLastRouterId() {
        return lastRouterId;
    }

    /**
     * Store an id in the updater book
     *
     * @param lastRouterId the id to store
     */
    public final void setLastRouterId(int lastRouterId) {
        this.lastRouterId = lastRouterId;
    }

    /**
     * @return the start
     */
    public Stack<Integer> getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    private void setStart(Stack<Integer> start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Stack<Integer> getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    private void setEnd(Stack<Integer> end) {
        this.end = end;
    }

    /**
     * Update the expiration time to have twice the spent time left
     */
    public void updateTimestamp() {
        long initial;
        long expiration;
        if ((initial = this.getCreationTime()) == (expiration = this.getExpirationTime())) {
            return;
        }
        long last = Calendar.getInstance().getTimeInMillis();
        long update = last + ((last - initial) << 1);
        if (update > expiration) {
            setExpirationTime(update);
        }
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Set the expiration time
     *
     * @param lastupdate the lastupdate to set
     */
    void setExpirationTime(long lastupdate) {
        this.expirationTime = lastupdate;
    }

    /**
     * Insert an entry in the IGP packet
     *
     * @param number the ring id
     * @param metric the metric value
     */
    public void setRoute(int number, int metric) {
        tableMap.put(number, new Metric(metric));
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : setting metric of ring " + number + " to " + metric);
        }
    }

    /**
     * Get the metric value of a ring of the IGP exchange packet
     *
     * @param entry the ring id
     * @return the metric
     */
    public int getMetric(int entry) {
        return tableMap.get(entry).value();
    }

    /**
     * Get the ring that has the minimum metric in the IGP packet
     *
     * @param routingTable the routing table
     * @param from the direction to exclude from the search
     * @return the ring id, or -1
     */
    public int getMinDistanceRing(RoutingTable routingTable, DirectionRegistry from) {
        Iterator<RouteValue> it = routingTable.getOrderedRouteNumbers();

        if (!it.hasNext()) {
            return -1;
        }

        //skip ring 0
        it.next();

        int route;
        int min = 10000, ret = -1; // big value

        while (it.hasNext()) {
            route = it.next().value();
            if (routingTable.getDirection(route).getAmount() != from.getAmount()) {
                if (!this.hasRouteTo(route)) {
                    if (ByteCartRedux.debug) {
                        ByteCartRedux.log.info("ByteCartRedux : found ring " + route + " was never visited");
                    }
                    return route;
                } else {
                    if (getMetric(route) < min) {
                        min = getMetric(route);
                        ret = route;
                    }
                }
            }
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : minimum found ring " + ret + " with " + min);
        }
        return ret;
    }

    /**
     * Tells if the IGP packet has data on a ring
     *
     * @param ring the ring id
     * @return true if there is data on this ring
     */
    public boolean hasRouteTo(int ring) {
        return tableMap.containsKey(ring);
    }
}
