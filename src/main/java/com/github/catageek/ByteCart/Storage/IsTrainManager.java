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
package com.github.catageek.ByteCart.Storage;

import org.bukkit.Location;

/**
 * A map that contain the train bit for each component
 *
 * The train bit set to true means that a train is currently using the component
 */
public final class IsTrainManager {

    private ExpirableMap<Location, Boolean> IsTrain = new ExpirableMap<Location, Boolean>(14, false, "isTrain");

    /**
     * Get the map
     *
     * @return the map
     */
    public ExpirableMap<Location, Boolean> getMap() {
        return IsTrain;
    }


}
