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
package com.github.catageek.bytecart.updater;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.event.custom.UpdaterClearRingEvent;
import com.github.catageek.bytecart.sign.BCSign;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;

final class UpdaterResetRegion extends UpdaterRegion implements Wanderer {

    UpdaterResetRegion(BCSign bc, UpdaterContent rte) {
        super(bc, rte);
    }

    @Override
    public void doAction(BlockFace to) {
        if (!this.isAtBorder()) {
            reset();
        }
    }

    @Override
    protected BlockFace selectDirection() {
        BlockFace face;
        if ((face = manageBorder()) != null) {
            return face;
        }
        return DefaultRouterWanderer.getRandomBlockFace(getRoutingTable(), getFrom().getBlockFace());
    }


    @Override
    protected final void reset() {
        // case of reset
        // erase address on sign if ring 0
        Address address = this.getSignAddress();
        boolean isValid = address.isValid();
        int track = this.getTrackNumber();

        if (!isValid || track == -1) {
            address.remove();
            if (isValid) {
                UpdaterClearRingEvent event = new UpdaterClearRingEvent(this, 0);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
        // clear routes except route to ring 0
        super.reset();
    }
}
