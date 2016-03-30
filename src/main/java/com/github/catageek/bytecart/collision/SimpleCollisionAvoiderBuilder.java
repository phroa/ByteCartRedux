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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A builder for simple collision avoider, i.e for a T cross-roads
 */
public class SimpleCollisionAvoiderBuilder extends AbstractCollisionAvoiderBuilder implements CollisionAvoiderBuilder {

    public SimpleCollisionAvoiderBuilder(Triggerable ic, Location<World> loc) {
        super(ic, loc);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends CollisionAvoider> T getCollisionAvoider() {

        return (T) new SimpleCollisionAvoider(this.ic, this.loc);
    }


}
