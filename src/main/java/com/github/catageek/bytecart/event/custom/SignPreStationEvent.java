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
package com.github.catageek.bytecart.event.custom;

import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.sign.Subnet;

/**
 * Event triggered when a vehicle is entering a station sign.
 *
 * This event is triggered before the "busy line" check, so the
 * direction may change.
 */
public class SignPreStationEvent extends SignPreSubnetEvent {

    /**
     * Default constructor
     *
     * The side parameter may be:
     * - LEFT: the vehicle wish not to enter the station
     * - RIGHT: the vehicle wish to enter the station
     *
     * @param subnet The BC9XXX sign involved
     * @param side The direction taken by the cart
     */
    public SignPreStationEvent(Subnet subnet, Side side) {
        super(subnet, side);
    }

}
