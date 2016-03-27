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
 * Event triggered when a local updater modifies the address
 * of a BC9001 sign.
 */
public class UpdaterSetStationEvent extends UpdaterClearStationEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address newAddress;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the station
     * @param newAddress The new address of the station
     * @param name The name of the station
     */
    public UpdaterSetStationEvent(Wanderer updater, Address oldAddress, Address newAddress, String name) {
        super(updater, oldAddress, name);
        this.newAddress = newAddress;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the newAddress
     */
    public Address getNewAddress() {
        return newAddress;
    }
}
