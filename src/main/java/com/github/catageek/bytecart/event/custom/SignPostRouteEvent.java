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

import com.github.catageek.bytecart.sign.BCRouter;
import com.github.catageek.bytecart.sign.BCSign;
import org.spongepowered.api.event.cause.Cause;


/**
 * Event triggered when a vehicle is leaving a router,
 * after the collision avoidance layer operations.
 *
 * The target track is definitive.
 */
public class SignPostRouteEvent extends BCRoutableSignEvent {

    private final BCRouter router;

    protected int to;

    /**
     * Default constructor
     *
     * @param router The router involved
     * @param to The ring number of the track where the vehicle is currently (not the destination ring)
     */
    public SignPostRouteEvent(BCRouter router, int to) {
        super(router);
        this.router = router;
        this.to = to;
    }

    /**
     * Get the track from where the vehicle entered the router
     *
     * @return The number of the ring
     */
    public final int getOriginTrack() {
        return router.getOriginTrack();
    }

    /**
     * Get the track to where the vehicle leaves the router
     *
     * @return The number of the ring
     */
    public final int getTargetTrack() {
        return to;
    }

    protected final BCSign getSign() {
        return router;
    }

    @Override
    public Cause getCause() {
        return Cause.source(getSign()).build();
    }
}
