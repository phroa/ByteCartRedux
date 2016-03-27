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

import com.github.catageek.bytecart.routing.RoutingTableWritable;
import com.github.catageek.bytecart.sign.BC8010;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.bukkit.block.BlockFace;

abstract class AbstractUpdater extends AbstractWanderer {

    private final RoutingTableWritable RoutingTable;

    protected AbstractUpdater(BCSign bc, int region) {
        super(bc, region);

        if (bc instanceof BC8010) {
            BC8010 ic = (BC8010) bc;
            RoutingTable = ic.getRoutingTable();
        } else {
            RoutingTable = null;
        }

    }

    /**
     * Get the direction where to go if we are at the border of a region or the backbone
     *
     * @return the direction where we must go
     */
    public final BlockFace manageBorder() {
        if ((isAtBorder())) {
            DirectionRegistry dir;
            if ((dir = this.getRoutingTable().getDirection(this.getWandererRegion())) != null) {
                return dir.getBlockFace();
            }
            return getFrom().getBlockFace();
        }
        return null;
    }

    /**
     * @return the routing table
     */
    protected final RoutingTableWritable getRoutingTable() {
        return RoutingTable;
    }
}
