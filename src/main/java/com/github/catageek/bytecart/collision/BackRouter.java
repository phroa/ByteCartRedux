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

import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.EnumSet;
import java.util.Set;

/**
 * A router where the cart goes back
 */
public final class BackRouter extends AbstractRouter implements Router {

    public BackRouter(Direction from, Location<World> loc) {
        super(from, loc);
        fromTo.put(Side.BACK, Side.BACK);

        Set<Side> left = EnumSet.of(Side.BACK, Side.LEFT);
        possibility.put(Side.LEFT, left);

        Set<Side> straight = EnumSet.of(Side.RIGHT, Side.LEFT, Side.BACK);
        possibility.put(Side.STRAIGHT, straight);

        Set<Side> right = EnumSet.of(Side.STRAIGHT, Side.BACK, Side.RIGHT);
        possibility.put(Side.RIGHT, right);

        setSecondpos(Integer.parseInt("10000000", 2));
        setPosmask(Integer.parseInt("11000001", 2));

    }

    @Override
    public final Direction getTo() {
        return this.getFrom();
    }

}
