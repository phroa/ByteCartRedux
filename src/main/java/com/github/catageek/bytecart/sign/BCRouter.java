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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.routing.RoutingTable;
import org.spongepowered.api.util.Direction;


/**
 * A router
 */
public interface BCRouter extends BCSign {

    /**
     * Get the track from where the cart is coming.
     *
     * For a region router, the returned value is the ring number.
     *
     * For a backbone router, the returned value is the region number.
     *
     * @return the track number
     */
    int getOriginTrack();

    /**
     * Return the direction from where the cart is coming
     *
     * @return the direction
     */
    Direction getFrom();

    RoutingTable getRoutingTable();
}
