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
package com.github.catageek.ByteCart.EventManagement;

import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.util.Vector;

/**
 * Listener to load chunks around moving carts
 */
public final class PreloadChunkListener implements Listener {

    private final Vector NullVector = new Vector(0, 0, 0);

    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {

        Location loc = event.getTo();
        int to_x = loc.getBlockX() >> 4;
        int to_z = loc.getBlockZ() >> 4;

        if (event.getVehicle() instanceof Minecart) // we care only of minecart
        {
            // preload chunks
            MathUtil.loadChunkAround(loc.getWorld(), to_x, to_z, 2);
        }
    }

    /**
     * We cancel this event if a cart is moving in the chunk or around
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {


        int n, j, i = event.getChunk().getX() - 2, k = i + 4, l = event.getChunk().getZ() + 2;
        World world = event.getWorld();

        Entity[] entities;

        for (; i <= k; ++i) {
            for (j = l - 4; j <= l; ++j) {

                if (world.isChunkLoaded(i, j)) {
                    entities = world.getChunkAt(i, j).getEntities();


                    for (n = entities.length - 1; n >= 0; --n) {
                        if (entities[n] instanceof Minecart && !((Minecart) entities[n]).getVelocity().equals(NullVector)) {

                            event.setCancelled(true);

                            return;
                        }
                    }
                }
            }
        }

    }


}
