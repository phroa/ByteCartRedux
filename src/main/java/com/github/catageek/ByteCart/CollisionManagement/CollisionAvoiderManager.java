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

import com.github.catageek.ByteCart.Storage.ExpirableMap;
import org.bukkit.Location;

/**
 * Manage the persistence of collision avoiders
 */
public final class CollisionAvoiderManager {

    /**
     * The map where collision avoiders are stored for 4 seconds
     */
    private final ExpirableMap<Location, CollisionAvoider> manager = new ExpirableMap<Location, CollisionAvoider>(40, false, "CollisionAvoider");

    /**
     * Get the map
     *
     * @return the map
     */
    private ExpirableMap<Location, CollisionAvoider> getManager() {
        return manager;
    }

    /**
     * Get an existing collision avoider, or create one
     *
     * @param builder a class providing an instance of the collision avoider
     * @return the collision avoider
     */
    @SuppressWarnings("unchecked")
    public final synchronized <T extends CollisionAvoider> T getCollisionAvoider(CollisionAvoiderBuilder builder) {
        Location loc = builder.getLocation();
        T cm;
        // Get an instance from the map
        cm = (T) this.getManager().get(loc);
        if (cm != null) {
            // if a collision avoider exists, attach itself as second IC
            cm.Add(builder.getIc());
            this.getManager().reset(loc);
        } else {
            // Get a new instance
            cm = builder.<T>getCollisionAvoider();
            // store the instance in the map
            this.getManager().put(loc, cm);
        }
        return cm;
    }

    /**
     * Set or replace the collision avoider stored at a specific location
     *
     * @param loc the location
     * @param ca the collision avoider to store
     */
    public final void setCollisionAvoider(Location loc, CollisionAvoider ca) {
        this.getManager().put(loc, ca);
    }


}
