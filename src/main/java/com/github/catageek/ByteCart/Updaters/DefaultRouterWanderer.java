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
package com.github.catageek.ByteCart.Updaters;


import com.github.catageek.ByteCart.CollisionManagement.IntersectionSide.Side;
import com.github.catageek.ByteCart.Signs.BCSign;
import org.bukkit.block.BlockFace;

/**
 *
 * This class implements a wanderer that will run through all routers
 * randomly, without going to branches.
 *
 * Wanderers implementors may extends this class and overrides its methods
 *
 */
public class DefaultRouterWanderer extends AbstractUpdater {

    public DefaultRouterWanderer(BCSign bc, int region) {
        super(bc, region);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Routing.AbstractWanderer#doAction(com.github.catageek.ByteCartRedux.CollisionManagement.SimpleCollisionAvoider
     * .Side)
     */
    @Override
    public void doAction(Side To) {
        return;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Routing.AbstractWanderer#doAction(org.bukkit.block.BlockFace)
     */
    @Override
    public void doAction(BlockFace To) {
    }


    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Routing.AbstractWanderer#giveSimpleDirection()
     */
    @Override
    public Side giveSimpleDirection() {
        return Side.LEVER_OFF;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Routing.AbstractWanderer#giveRouterDirection()
     */
    @Override
    public BlockFace giveRouterDirection() {
        return getRandomBlockFace(this.getRoutingTable(), this.getFrom().getBlockFace());
    }

}
