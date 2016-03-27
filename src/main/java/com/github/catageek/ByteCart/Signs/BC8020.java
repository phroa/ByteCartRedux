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
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.AddressLayer.AddressRouted;
import com.github.catageek.ByteCart.Routing.RoutingTableWritable;
import com.github.catageek.ByteCart.Updaters.DefaultRouterWanderer;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import com.github.catageek.ByteCart.Wanderer.Wanderer.Scope;
import com.github.catageek.ByteCart.Wanderer.WandererContentFactory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.IOException;


/**
 * An IC at the entry of a L2 router
 */
final class BC8020 extends BC8010 implements BCRouter, Triggable, HasRoutingTable {


    BC8020(Block block, org.bukkit.entity.Vehicle vehicle) throws ClassNotFoundException, IOException {
        super(block, vehicle);
        this.IsTrackNumberProvider = true;
    }

    @Override
    protected boolean selectWanderer() {
        return (!WandererContentFactory.isWanderer(this.getInventory()))
                || WandererContentFactory.isWanderer(this.getInventory(), Scope.LOCAL);
    }

     */
    @Override
    protected BlockFace SelectRoute(AddressRouted IPaddress, Address sign, RoutingTableWritable RoutingTable) {

        try {
            if (IPaddress.getTTL() != 0) {
                // lookup destination region
                return RoutingTable.getDirection(IPaddress.getRegion().getAmount()).getBlockFace();
            }
        } catch (NullPointerException e) {
        }

        // if TTL reached end of life and is not returnable, then we lookup region 0
        try {
            return RoutingTable.getDirection(0).getBlockFace();
        } catch (NullPointerException e) {
        }

        // If everything has failed, then we randomize output direction
        return DefaultRouterWanderer.getRandomBlockFace(RoutingTable, getCardinal().getOppositeFace());

    }

    @Override
    public Wanderer.Level getLevel() {
        return Wanderer.Level.BACKBONE;
    }

    @Override
    public String getName() {
        return "BC8020";
    }

    @Override
    public String getFriendlyName() {
        return "L2 Router";
    }
}
