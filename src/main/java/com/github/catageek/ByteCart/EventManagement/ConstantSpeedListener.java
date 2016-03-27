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
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener to maintain cart speed
 */
public final class ConstantSpeedListener implements Listener {

    // We keep the speed of each cart in this map
    private final Map<Integer, Double> speedmap = new HashMap<Integer, Double>();

    // empty Location
    private Location location = new Location(null, 0, 0, 0);

    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle v = event.getVehicle();

        if (!(v instanceof Minecart)) {
            return;
        }

        Minecart m = (Minecart) v;
        double speed = MathUtil.getSpeed(m);
        int id = m.getEntityId();

        MaterialData data = m.getLocation(location).getBlock().getState().getData();

        if (speed != 0 && (data instanceof org.bukkit.material.Rails)) {
            Double storedspeed;
            if (!speedmap.containsKey(id)) {
                speedmap.put(id, speed);
            } else if ((storedspeed = speedmap.get(id)) > speed
                    && storedspeed <= m.getMaxSpeed()) {
                MathUtil.setSpeed(m, storedspeed);
            } else {
                speedmap.put(id, speed);
            }
        } else {
            speedmap.remove(id);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        speedmap.remove(event.getVehicle().getEntityId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        speedmap.remove(event.getVehicle().getEntityId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        speedmap.remove(event.getVehicle().getEntityId());
    }


}
