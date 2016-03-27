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

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a region reset updater clears the address
 * of a BC9001 sign.
 */
public class UpdaterClearStationEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address oldAddress;
    private final String name;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the station
     * @param name The name of the station
     */
    public UpdaterClearStationEvent(Wanderer updater, Address oldAddress, String name) {
        super(updater);
        this.oldAddress = oldAddress;
        this.name = name;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the old address erased
     *
     * @return A String containing the address
     */
    public String getOldAddress() {
        return oldAddress.toString();
    }

    /**
     * Get the name of the station
     *
     * @return The name
     */
    public String getName() {
        return name;
    }
}
