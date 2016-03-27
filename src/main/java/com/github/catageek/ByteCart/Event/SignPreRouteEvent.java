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

import com.github.catageek.ByteCart.Signs.BCRouter;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a vehicle is entering a router,
 * before the collision avoidance layer operations.
 *
 * The target track may be changed by collision avoidance layer.
 */
public final class SignPreRouteEvent extends SignPostRouteEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Default constructor
     *
     * @param router The router involved
     * @param to The ring number of the connected track where the vehicle wish to go (not the destination ring)
     */
    public SignPreRouteEvent(BCRouter router, int to) {
        super(router, to);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Modify the destination ring on the fly to override the routing layer.
     * This will modify internal state of the router before actual operations.
     * This will not change the destination address recorded in the vehicle.
     *
     * The final route is undefined until routing layer operations occur.
     *
     * @param target The ring to send the vehicle to
     */
    public final void setTargetTrack(int target) {
        to = target;
    }
}
