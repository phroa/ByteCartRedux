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

import com.github.catageek.bytecart.hardware.IC;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.Wanderer.Level;

/**
 * An event concerning an updater.
 * Implementations must inherit this class.
 */
abstract public class UpdaterEvent extends BCEvent {

    private final Wanderer updater;

    /**
     * Default constructor
     *
     * @param updater involved in the event
     */
    UpdaterEvent(Wanderer updater) {
        super(updater.getBcSign());
        this.updater = updater;
    }

    protected final IC getSign() {
        return updater.getBcSign();
    }

    /**
     * @return The track number currently in use, or -1 if invalid
     */
    public final int getCurrentTrack() {
        return updater.getTrackNumber();
    }

    /**
     * Get the level of the updater.
     *
     * 	Possible values are:
     * 	- LOCAL or RESET_LOCAL
     *  - REGION or RESET_REGION
     *  - BACKBONE or RESET_BACKBONE
     *
     * @return The level of the updater.
     */
    public final Level getUpdaterLevel() {
        return updater.getLevel();
    }

    /**
     * @return The region where the updater is registered.
     * returns 0 for backbone.
     */
    public final int getUpdaterRegion() {
        return updater.getWandererRegion();
    }

    /**
     * @return the entity id of the vehicle
     */
    public final int getVehicleId() {
        return updater.getVehicle().getEntityId();
    }

    /**
     * @return The updater involved
     */
    protected final Wanderer getUpdater() {
        return updater;
    }
}
