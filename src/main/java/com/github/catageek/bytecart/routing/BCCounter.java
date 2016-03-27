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
package com.github.catageek.bytecart.routing;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.util.DirectionRegistry;
import com.github.catageek.bytecart.updater.Counter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A map containing counters with id
 */
public final class BCCounter implements Serializable, Counter {

    /**
     *
     */
    private static final long serialVersionUID = 6858180714411403984L;
    private final Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    public BCCounter() {
    }

    /**
     * Get a counter by its id
     *
     * @param counter
     * @return the counter
     */
    public int getCount(int counter) {
        return this.map.containsKey(counter) ? this.map.get(counter) : 0;
    }

    /**
     * Increment the counter by 1
     *
     * @param counter the counter id
     */
    public void incrementCount(int counter) {
        incrementCount(counter, 1);
    }

    /**
     * Add a value to a counter
     *
     * @param counter the counter id
     * @param value the value to add
     */
    public void incrementCount(int counter, int value) {
        this.map.put(counter, getCount(counter) + value);
    }


    /**
     * Set the value of a counter
     *
     * @param counter the counter id
     * @param amount the value to set
     */
    public void setCount(int counter, int amount) {
        this.map.put(counter, amount);
    }

    /**
     * Get the first empty counter id
     *
     * @return the id
     */
    public int firstEmpty() {
        int i = 1;
        while (getCount(i) != 0) {
            i++;
        }
        return i;
    }

    /**
     * Reset a counter to zero
     *
     * @param counter the id of the counter
     */
    public void reset(int counter) {
        this.map.remove(counter);
    }

    /**
     * Reset all counters to zero
     */
    public void resetAll() {
        this.map.clear();
    }

    /**
     * Tell if counters have reached the amount of 64 or more
     *
     * @param start the first counter id
     * @param end the last counter id
     * @return true if the amont of all counters between start and end (inclusive) are equal or superior to 64
     */
    public boolean isAllFull(int start, int end) {
        Iterator<Integer> it = map.keySet().iterator();
        int limit = 64;
        int count;
        while (it.hasNext()) {
            if ((count = it.next()) >= start && count <= end && map.get(count) < limit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the ring number that has the minimum counter value and the direction is different from from parameter
     *
     * @param routes the routing table
     * @param from the direction to exclude from the search
     * @return the ring number, or 0 if no result
     */
    public int getMinimum(RoutingTableWritable routes, DirectionRegistry from) {
        Iterator<RouteValue> it = routes.getOrderedRouteNumbers();
        int min = 10000000;  //big value
        int index = -1;
        while (it.hasNext()) {
            int ring = it.next().value();

            if (ring == 0) {
                continue;
            }

            if (!map.containsKey(ring)) {
                return ring;
            }

            int value;
            if ((value = map.get(ring)) < min
                    && routes.getDirection(ring).getAmount() != from.getAmount()
                    && ring != 0) {
                min = value;
                index = ring;
            }
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : minimum found ring " + index + " with " + min);
        }
        return index;

    }

    @Override
    public final String toString() {
        Iterator<Integer> it = map.keySet().iterator();
        String s = "";
        while (it.hasNext()) {
            int ring = it.next();
            int count = map.get(ring);
            s += "ByteCartRedux: Count for ring " + ring + " = " + count + "\n";
        }
        return s;
    }

    /**
     * @return the number of counters the map can contain
     */
    public int getCounterLength() {
        return 32;
    }


}
