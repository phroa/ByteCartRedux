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

import com.github.catageek.bytecart.updater.UpdaterContent;
import com.github.catageek.bytecart.util.DirectionRegistry;

import java.io.IOException;


/**
 * A routing table
 */
public interface RoutingTableWritable extends RoutingTable {

    /**
     * Store a line in the routing table
     *
     * @param entry the track number
     * @param direction the direction to associate
     * @param metric the metric to associate
     */
    void setEntry(int entry, DirectionRegistry direction, Metric metric);

    /**
     * Remove a line from the routing table
     *
     * @param entry the track number
     * @param from the direction to remove
     */
    void removeEntry(int entry, DirectionRegistry from);

    /**
     * Performs the IGP protocol to update the routing table
     *
     * @param neighbour the IGP packet received
     * @param from the direction from where we received it
     */
    void update(UpdaterContent neighbour, DirectionRegistry from);

    /**
     * Clear the routing table
     *
     * @param fullreset if set to false, route to entry 0 is kept.
     */
    void clear(boolean fullreset);


    /**
     * Serialize the routing table
     *
     * @throws IOException
     */
    void serialize() throws IOException;
}
