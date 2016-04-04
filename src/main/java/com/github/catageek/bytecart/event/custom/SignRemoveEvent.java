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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

/**
 * Event triggered when a sign is physically removed from the world
 */
public final class SignRemoveEvent extends BCEvent {

    private final Entity entity;

    public SignRemoveEvent(IC ic, Entity entity) {
        super(ic);
        this.entity = entity;
    }

    /**
     * @return the entity that broke the block
     */
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Cause getCause() {
        return Cause.source(entity).build();
    }
}
