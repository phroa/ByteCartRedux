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

public final class UpdaterRemoveEvent implements Event {

    private final int vehicleId;

    /**
     * Default constructor
     *
     * @param vehicleId the vehicle id
     * @param location the location
     */
    public UpdaterRemoveEvent(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * @return the vehicleId
     */
    public int getVehicleId() {
        return vehicleId;
    }

    @Override
    public Cause getCause() {
        return Cause.source(vehicleId).build();
    }
}
