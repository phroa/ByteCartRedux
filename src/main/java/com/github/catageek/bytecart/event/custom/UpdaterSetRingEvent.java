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

import com.github.catageek.bytecart.updater.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a region updater modifies the address
 * on a BC8010 sign.
 */
public class UpdaterSetRingEvent extends UpdaterClearRingEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int newring;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param old The old value of the ring
     * @param newring The new value of the ring
     */
    public UpdaterSetRingEvent(Wanderer updater, int old, int newring) {
        super(updater, old);
        this.newring = newring;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the newring
     */
    public int getNewring() {
        return newring;
    }
}
