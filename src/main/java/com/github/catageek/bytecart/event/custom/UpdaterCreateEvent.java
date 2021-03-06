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

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Event triggered when an updater is created
 */
public final class UpdaterCreateEvent implements Event {

    private final UUID vehicleId;
    private final Location<World> location;

    /**
     * Default constructor
     *
     * @param vehicleId the vehicle id
     * @param location the location
     */
    public UpdaterCreateEvent(UUID vehicleId, Location<World> location) {
        this.vehicleId = vehicleId;
        this.location = location;
    }

    /**
     * @return the vehicleId
     */
    public UUID getVehicleId() {
        return vehicleId;
    }

    /**
     * @return the location
     */
    public Location<World> getLocation() {
        return location;
    }

    @Override
    public Cause getCause() {
        return Cause.source(vehicleId).build();
    }
}
