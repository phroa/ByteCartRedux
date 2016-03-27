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
package com.github.catageek.ByteCart.CollisionManagement;

import com.github.catageek.ByteCart.Signs.Triggable;
import org.bukkit.Location;

/**
 * A builder for a collision manager
 */
public interface CollisionAvoiderBuilder {

    /**
     * Get an instance of the collision manager
     *
     * @return an instance of collision manager
     */
    public <T extends CollisionAvoider> T getCollisionAvoider();

    /**
     * Get the location to where the collision managers built will be attached
     *
     *
     * @return the location
     */
    public Location getLocation();

    /**
     * Get the IC attached to the collision managers built
     *
     *
     * @return the IC
     */
    public Triggable getIc();

}
