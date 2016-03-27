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

import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.Set;

/**
 * A router where the cart goes back
 */
public final class BackRouter extends AbstractRouter implements
        Router {

    public BackRouter(BlockFace from, org.bukkit.Location loc) {
        super(from, loc);
        FromTo.put(Side.BACK, Side.BACK);

        Set<Side> left = EnumSet.of(Side.BACK, Side.LEFT);
        Possibility.put(Side.LEFT, left);

        Set<Side> straight = EnumSet.of(Side.RIGHT, Side.LEFT, Side.BACK);
        Possibility.put(Side.STRAIGHT, straight);

        Set<Side> right = EnumSet.of(Side.STRAIGHT, Side.BACK, Side.RIGHT);
        Possibility.put(Side.RIGHT, right);

        setSecondpos(Integer.parseInt("10000000", 2));
        setPosmask(Integer.parseInt("11000001", 2));

    }

    @Override
    public final BlockFace getTo() {
        return this.getFrom();
    }

}
