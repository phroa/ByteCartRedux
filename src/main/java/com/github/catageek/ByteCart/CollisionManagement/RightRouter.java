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

import com.github.catageek.ByteCart.Util.DirectionRegistry;
import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.block.BlockFace;

/**
 * A router where the cart turns right
 */
public final class RightRouter extends AbstractRouter implements
        Router {

    public RightRouter(BlockFace from, org.bukkit.Location loc) {
        super(from, loc);

        FromTo.put(Side.BACK, Side.RIGHT);
        FromTo.put(Side.LEFT, Side.LEFT);
        FromTo.put(Side.STRAIGHT, Side.STRAIGHT);
        FromTo.put(Side.RIGHT, Side.BACK);

        setSecondpos(Integer.parseInt("00101001", 2));


    }

    @Override
    public void route(BlockFace from) {
        // activate main levers
        this.getOutput(0).setAmount((new DirectionRegistry(MathUtil.anticlockwise(from))).getAmount());

    }

    @Override
    public BlockFace getTo() {
        return MathUtil.anticlockwise(this.getFrom());
    }

}
