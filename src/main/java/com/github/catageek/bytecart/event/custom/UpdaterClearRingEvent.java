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
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a region reset updater clears the address
 * of a BC8010 sign.
 */
public class UpdaterClearRingEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int old;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param old The old value of the ring
     */
    public UpdaterClearRingEvent(Wanderer updater, int old) {
        super(updater);
        this.old = old;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the face of the router where the sign is cleared
     *
     * @return The face of the router
     */
    public final BlockFace getFrom() {
        return getUpdater().getFrom().getBlockFace();
    }

    /**
     * @return The ring number
     */
    public int getOldRing() {
        return old;
    }
}
