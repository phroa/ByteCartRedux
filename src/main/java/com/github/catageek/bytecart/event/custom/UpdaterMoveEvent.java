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
import org.spongepowered.api.event.entity.DisplaceEntityEvent;

/**
 * Event trigger when an updater moves from 1 block
 */
public final class UpdaterMoveEvent implements Event {

    private final DisplaceEntityEvent.Move event;

    public UpdaterMoveEvent(DisplaceEntityEvent.Move event) {
        this.event = event;
    }

    /**
     * @return the event
     */
    public DisplaceEntityEvent.Move getEvent() {
        return event;
    }

    @Override
    public Cause getCause() {
        return event.getCause();
    }

}
