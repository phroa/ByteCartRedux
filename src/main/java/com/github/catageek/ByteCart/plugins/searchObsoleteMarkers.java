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
package com.github.catageek.ByteCart.plugins;

import com.github.catageek.ByteCart.HAL.AbstractIC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.dynmap.markers.Marker;

import java.util.Iterator;

/**
 * Synchronous task to remove markers
 */
public final class searchObsoleteMarkers implements Runnable {

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Iterator<Marker> it = BCDynmapPlugin.markerset.getMarkers().iterator();
        int x, y, z;
        while (it.hasNext()) {
            Marker m = it.next();
            x = Location.locToBlock(m.getX());
            y = Location.locToBlock(m.getY());
            z = Location.locToBlock(m.getZ());
            Block block = Bukkit.getServer().getWorld(m.getWorld()).getBlockAt(x, y, z);
            if (!AbstractIC.checkEligibility(block)) {
                m.deleteMarker();
            }
        }
    }
}

