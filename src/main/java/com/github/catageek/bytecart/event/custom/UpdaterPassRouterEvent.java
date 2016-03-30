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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.util.Direction;

/**
 * Event triggered when an updater pass a router of the same level.
 * A region updater will trigger this event on BC8010 sign.
 * A backbone updater will trigger this event on BC8020 sign.
 * A local updater will never trigger this event
 */
public class UpdaterPassRouterEvent extends UpdaterEvent {

    private final int nextRing;
    private final Direction to;
    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param to The face from where the updater will leave the router
     * @param nextring The ring number of the track attached to the router where it is going
     */
    public UpdaterPassRouterEvent(Wanderer updater, Direction to, int nextring) {
        super(updater);
        this.to = to;
        this.nextRing = nextring;
    }

    /**
     * Get the face of the router from where the updater comes
     *
     * @return The face of the updater
     */
    public final Direction getFrom() {
        return getUpdater().getFrom().getBlockFace();
    }

    /**
     * @return The ring number of the track attached to the router where it is going
     */
    public int getNextRing() {
        return nextRing;
    }

    /**
     * @return The face from where the updater will leave the router
     */
    public Direction getTo() {
        return to;
    }

    /**
     * @return The center of the router, at sign level
     */
    public BlockSnapshot getCenter() {
        return getUpdater().getCenter();
    }


}
