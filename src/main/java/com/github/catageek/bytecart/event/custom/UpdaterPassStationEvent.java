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

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.updater.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when an local updater pass a station sign.
 */

public class UpdaterPassStationEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address address;
    private final String name;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param address The address of the station
     * @param name The name of the station
     */
    public UpdaterPassStationEvent(Wanderer updater, Address address, String name) {
        super(updater);
        this.address = address;
        this.name = name;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The address of the station
     */
    public Address getAddress() {
        return address;
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
