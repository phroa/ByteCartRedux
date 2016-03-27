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

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when an updater is created
 */
public final class UpdaterCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final int vehicleId;
    private final Location location;

    /**
     * Default constructor
     *
     * @param VehicleId the vehicle id
     * @param location the location
     */
    public UpdaterCreateEvent(int VehicleId, Location location) {
        this.vehicleId = VehicleId;
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the vehicleId
     */
    public int getVehicleId() {
        return vehicleId;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }
}
