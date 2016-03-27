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
 * Event triggered when a region reset updater clears the address
 * of a BC9XXX sign (except BC9001).
 */
public class UpdaterClearSubnetEvent extends UpdaterClearStationEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int length;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the subnet
     * @param length number of stations this subnet can contain
     */
    public UpdaterClearSubnetEvent(Wanderer updater, Address oldAddress, int length) {
        super(updater, oldAddress, "");
        this.length = length;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the numbers of address this subnet can contain
     *
     * @return The number of address
     */
    public int getLength() {
        return length;
    }
}
