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

import com.github.catageek.bytecart.util.DirectionRegistry;
import org.bukkit.block.BlockFace;

/**
 * A router where the cart goes straight
 */
public class StraightRouter extends AbstractRouter implements Router {

    public StraightRouter(BlockFace from, org.bukkit.Location loc) {
        super(from, loc);

        FromTo.put(Side.BACK, Side.STRAIGHT);
        FromTo.put(Side.LEFT, Side.LEFT);
        FromTo.put(Side.STRAIGHT, Side.RIGHT);
        FromTo.put(Side.RIGHT, Side.BACK);

        setSecondpos(Integer.parseInt("00100101", 2));


    }

    @Override
    public void route(BlockFace from) {
        // activate main levers
        this.getOutput(0).setAmount((new DirectionRegistry(from.getOppositeFace())).getAmount());

    }

    @Override
    public final BlockFace getTo() {
        return this.getFrom().getOppositeFace();
    }
}
