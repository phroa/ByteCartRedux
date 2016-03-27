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

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.ByteCart;
import com.github.catageek.ByteCart.Signs.BCSign;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.block.BlockFace;

class UpdaterBackBone extends AbstractRegionUpdater implements Wanderer {

    UpdaterBackBone(BCSign bc, UpdaterContent rte) {
        super(bc, rte);
    }


    protected BlockFace selectDirection() {
        BlockFace face;
        if ((face = manageBorder()) != null) {
            return face;
        }

        return DefaultRouterWanderer.getRandomBlockFace(this.getRoutingTable(), this.getFrom().getBlockFace());
    }

    @Override
    public void Update(BlockFace To) {

        // current: track number we are on
        int current = getCurrent();

        if (getRoutes() != null) {

            if (getSignAddress().isValid()) {
                // there is an address on the sign
                if (ByteCart.debug) {
                    ByteCart.log.info("ByteCart : track number is " + getTrackNumber());
                }
                setCurrent(getTrackNumber());

                if (ByteCart.debug) {
                    ByteCart.log.info("ByteCart : current is " + current);
                }
            } else
                // no address on sign, and is not provider
                // assumes it's 0 if first sign met
                if (current == -2) {
                    setCurrent(0);
                }

            routeUpdates(To);

        }
    }

    @Override
    public final int getTrackNumber() {
        Address address;
        if ((address = getSignAddress()).isValid()) {
            return address.getRegion().getAmount();
        }
        return -1;
    }

}
