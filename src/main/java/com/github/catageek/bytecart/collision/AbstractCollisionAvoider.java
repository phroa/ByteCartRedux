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
package com.github.catageek.bytecart.collision;

import com.github.catageek.bytecart.collection.ExpirableMap;
import com.github.catageek.bytecart.hardware.AbstractIC;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Abstract class for collision avoiders
 */
public abstract class AbstractCollisionAvoider extends AbstractIC {

    /**
     * @param loc the location where the collision avoider will be attached
     */
    AbstractCollisionAvoider(Location<World> loc) {
        super(loc.createSnapshot());
    }

    /**
     * Get a map of locations that have been recently used
     *
     * @return the map
     */
    abstract protected ExpirableMap<Location<World>, Boolean> getRecentlyUsedMap();

    /**
     * Get a map of locations that have the train flag
     *
     *
     * @return the map
     */
    abstract protected ExpirableMap<Location<World>, Boolean> getHasTrainMap();

    /**
     * Tell if this collision avoider has the train flag set
     *
     * @return true if the flag is set
     */
    boolean getHasTrain() {
        return this.getHasTrainMap().contains(getLocation());
    }

    /**
     * @param hasTrain the hasTrain to set
     */
    private void setHasTrain(boolean hasTrain) {
        this.getHasTrainMap().put(getLocation(), hasTrain);
    }

    /**
     * Tell if this collision avoider has been recently used
     *
     * @return true if recently used
     */
    boolean getRecentlyUsed() {
        return this.getRecentlyUsedMap().contains(getLocation());
    }

    /**
     * @param recentlyUsed the recentlyUsed to set
     */
    void setRecentlyUsed(boolean recentlyUsed) {
        this.getRecentlyUsedMap().put(getLocation(), recentlyUsed);
    }

    /**
     * {@link Router#book(boolean)}
     */
    public void book(boolean isTrain) {
        setRecentlyUsed(true);
        setHasTrain(this.getHasTrain() | isTrain);
    }

    @Override
    public final String getName() {
        return "Collision avoider";
    }

    @Override
    public final String getFriendlyName() {
        return getName();
    }

    /**
     * Relative direction for the router. BACK is the direction from where the cart is arriving
     */
    public enum Side {
        BACK(0),
        LEFT(2),
        STRAIGHT(4),
        RIGHT(6);

        private final int Value;

        Side(int b) {
            Value = b;
        }

        public int Value() {
            return Value;
        }
    }

}
