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

import com.github.catageek.bytecart.sign.Triggerable;
import org.bukkit.Location;

/**
 * Abstract class for colllision avoider builders
 */
public abstract class AbstractCollisionAvoiderBuilder {

    /**
     * The first IC attached to the collision avoiders created
     */
    protected final Triggerable ic;

    /**
     * The location to where the collision avoiders will be attached
     */
    protected final Location loc;

    public AbstractCollisionAvoiderBuilder(Triggerable ic, Location loc) {
        this.ic = ic;
        this.loc = loc;
    }

    /**
     * Get the location to where the collision avoiders created will be attached
     *
     * @return the location
     */
    public Location getLocation() {
        return this.loc;
    }

    /**
     * Get the IC to which the collision avoiders created will be attached
     *
     *
     * @return the IC
     */
    public Triggerable getIc() {
        return ic;
    }

}
