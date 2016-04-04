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
package com.github.catageek.bytecart.event;

import com.flowpowered.math.vector.Vector3d;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Optional;

/**
 * Listener to load chunks around moving carts
 */
public final class PreloadChunkListener {

    @Listener
    public void onVehicleMove(DisplaceEntityEvent.Move event, @Root Minecart vehicle) {

        Location<World> loc = event.getToTransform().getLocation();
        int toX = loc.getBlockX() >> 4;
        int toZ = loc.getBlockZ() >> 4;

        // preload chunks
        MathUtil.loadChunkAround(loc.getExtent(), toX, toZ, 2);
    }

    /**
     * We cancel this event if a cart is moving in the chunk or around
     *
     * @param event
     */
    @Listener(order = Order.LAST)
    public void onChunkUnload(UnloadChunkEvent event) {


        int n, j, i = event.getTargetChunk().getPosition().getX() - 2, k = i + 4, l = event.getTargetChunk().getPosition().getZ() + 2;
        World world = event.getTargetChunk().getWorld();

        Entity[] entities;

        for (; i <= k; ++i) {
            for (j = l - 4; j <= l; ++j) {

                Optional<Chunk> chunk = world.getChunk(i, event.getTargetChunk().getPosition().getY(), j);
                if (chunk.map(Extent::isLoaded).orElse(false)) {
                    entities = chunk.get().getEntities().toArray(new Entity[0]);

                    for (n = entities.length - 1; n >= 0; --n) {
                        if (entities[n] instanceof Minecart && !((Minecart) entities[n]).getVelocity().equals(Vector3d.ZERO)) {

                            Sponge.getServer().getChunkTicketManager().createTicket(ByteCartRedux.myPlugin, world).get()
                                    .forceChunk(chunk.get().getPosition());
                            return;
                        }
                    }
                }
            }
        }

    }


}
