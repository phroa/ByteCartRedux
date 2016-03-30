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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.routing.RoutingTableWritable;
import com.github.catageek.bytecart.updater.DefaultRouterWanderer;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.Wanderer.Scope;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

import java.io.IOException;


/**
 * An IC at the entry of a L2 router
 */
final class BC8020 extends BC8010 implements BCRouter, Triggerable, HasRoutingTable {


    BC8020(BlockSnapshot block, Entity vehicle) throws ClassNotFoundException, IOException {
        super(block, vehicle);
        this.isTrackNumberProvider = true;
    }

    @Override
    protected boolean selectWanderer() {
        return (!WandererContentFactory.isWanderer(this.getInventory()))
                || WandererContentFactory.isWanderer(this.getInventory(), Scope.LOCAL);
    }

    @Override
    protected Direction selectRoute(AddressRouted destination, Address sign, RoutingTableWritable routingTable) {

        try {
            if (destination.getTTL() != 0) {
                // lookup destination region
                return routingTable.getDirection(destination.getRegion().getValue()).getBlockFace();
            }
        } catch (NullPointerException e) {
        }

        // if TTL reached end of life and is not returnable, then we lookup region 0
        try {
            return routingTable.getDirection(0).getBlockFace();
        } catch (NullPointerException e) {
        }

        // If everything has failed, then we randomize output direction
        return DefaultRouterWanderer.getRandomBlockFace(routingTable, getCardinal().getOpposite());

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
