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
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.sign.Subnet;
import org.spongepowered.api.event.cause.Cause;

/**
 * Event triggered when a vehicle is using a subnet sign,
 * after the collision avoidance layer operations.
 *
 * The direction is definitive.
 */
public class SignPostSubnetEvent extends BCRoutableSignEvent {

    protected final Subnet subnet;
    protected Side side;
    /**
     * Default constructor
     *
     * The side parameter may be:
     * - LEFT: the vehicle will not enter the subnet OR will not leave the subnet if it was inside
     * - RIGHT: the vehicle enters the subnet OR leaves the subnet if it was inside
     *
     * @param subnet The BC9XXX sign involved
     * @param side The direction taken by the vehicle
     */
    public SignPostSubnetEvent(Subnet subnet, Side side) {
        super(subnet);
        this.subnet = subnet;
        this.side = side;
    }

    @Override
    protected BCSign getSign() {
        return subnet;
    }

    /**
     * Get the direction taken by the vehicle
     *
     * @return A value from IntersectionSide.Side enum
     */
    public Side getSide() {
        return side;
    }

    /**
     * Get the mask of the subnet.
     *
     * Possible values are:
     * 	- 0: No mask (BC9000)
     *  - 5: 8-station subnet (BC9008)
     *  - 6: 4-station subnet (BC9004)
     *  - 7: 2-station subnet (BC9002)
     *  - 8: station (BC9001)
     *
     * @return The mask of the subnet
     */
    public int getNetmask() {
        return subnet.getNetmask();
    }

    @Override
    public Cause getCause() {
        return Cause.source(getSign()).build();
    }
}
