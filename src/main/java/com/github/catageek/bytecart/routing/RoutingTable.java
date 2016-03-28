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

import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.util.Direction;

import java.util.Iterator;
import java.util.Set;

public interface RoutingTable {

    /**
     * Return the best direction matching the entry
     *
     * @param entry the track number
     * @return the direction
     */
    DirectionRegistry getDirection(int entry);

    /**
     * Get the metric associated with this entry and this direction
     *
     * @param entry the track number
     * @param direction the direction
     * @return the metric
     */
    int getMetric(int entry, DirectionRegistry direction);

    /**
     * Get the minimum metric for a specific entry
     *
     * @param entry the track number
     * @return the minimum metric recorded, or -1
     */
    int getMinMetric(int entry);

    /**
     * Tells if there is no record for an entry
     *
     * @param entry the track number
     * @return true if there is no record
     */
    boolean isEmpty(int entry);

    /**
     * Tells if a track is directly connected to a router at a specific direction
     *
     * @param ring the track number
     * @param direction the direction
     * @return true if the track is directly connected at this direction
     */
    boolean isDirectlyConnected(int ring, DirectionRegistry direction);

    /**
     * Get the track number at the specific direction
     *
     * @param direction the direction
     * @return the track number
     */
    int getDirectlyConnected(DirectionRegistry direction);

    /**
     * Get a direction that has not been configured, or null if all directions are configured
     *
     * @return the direction
     */
    Direction getFirstUnknown();

    /**
     * Get the number of entries in the routing table
     *
     * @return the size
     */
    int size();

    /**
     * Get a list of tracks that have records with a metric 0 and at the specific direction
     *
     * @param from the direction
     * @return a list of track numbers
     */
    Set<Integer> getDirectlyConnectedList(DirectionRegistry from);

    /**
     * Return an iterator of Route in incrementing order
     *
     *
     * @return the set
     */
    <T extends RouteValue> Iterator<T> getOrderedRouteNumbers();

}
