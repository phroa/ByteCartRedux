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

import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener to maintain cart speed
 */
public final class ConstantSpeedListener {

    // We keep the speed of each cart in this map
    private final Map<UUID, Double> speedMap = new HashMap<>();

    // empty Location
    private Location<World> location;

    @Listener
    public void onVehicleMove(DisplaceEntityEvent.Move event, @Root Minecart m) {
        double speed = MathUtil.getSpeed(m);
        UUID id = m.getUniqueId();

        BlockState block = (location = m.getLocation()).getBlock();

        if (speed != 0 && block.supports(Keys.RAIL_DIRECTION)) {
            Double storedSpeed;
            if (!speedMap.containsKey(id)) {
                speedMap.put(id, speed);
            } else if ((storedSpeed = speedMap.get(id)) > speed
                    && storedSpeed <= m.getPotentialMaxSpeed()) {
                MathUtil.setSpeed(m, storedSpeed);
            } else {
                speedMap.put(id, speed);
            }
        } else {
            speedMap.remove(id);
        }
    }

    @Listener(order = Order.POST)
    public void onVehicleDestroy(DestructEntityEvent event, @Root Minecart minecart) {
        speedMap.remove(minecart.getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onVehicleEntityCollision(CollideEntityEvent event, @First Minecart minecart) {
        speedMap.remove(minecart.getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onVehicleBlockCollision(CollideBlockEvent event, @First Minecart minecart) {
        speedMap.remove(minecart.getUniqueId());
    }


}
