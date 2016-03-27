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
package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.CollisionManagement.IntersectionSide.Side;
import com.github.catageek.ByteCart.Signs.BCSign;
import com.github.catageek.ByteCart.Signs.Subnet;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a vehicle is using a subnet sign,
 * before the collision avoidance layer operations.
 *
 * The direction may be modified by collision avoidance layer.
 */
public class SignPreSubnetEvent extends SignPostSubnetEvent {

    private static final HandlerList handlers = new HandlerList();


    /**
     * Default constructor
     *
     * The side parameter may be:
     * - LEFT: the vehicle wish not to enter the subnet
     * - RIGHT: the vehicle wish to enter the subnet OR wish to leave the subnet if it was inside
     *
     * @param subnet The BC9XXX sign involved
     * @param side The direction wished of the vehicle
     */
    public SignPreSubnetEvent(Subnet subnet, Side side) {
        super(subnet, side);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    protected BCSign getSign() {
        return subnet;
    }

    /**
     * Change the direction taken by the vehicle on the fly
     * This will modify internal state of the sign before actual operations.
     * This will not change the destination address recorded in the vehicle.
     *
     * The final direction is undefined until routing layer operations occur.
     *
     * @param side A value from IntersectionSide.Side enum
     */
    public void setSide(Side side) {
        this.side = side;
    }
}
