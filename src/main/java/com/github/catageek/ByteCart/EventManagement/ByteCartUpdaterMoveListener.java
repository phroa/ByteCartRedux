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

import com.github.catageek.ByteCart.Event.UpdaterMoveEvent;
import com.github.catageek.ByteCart.Event.UpdaterRemoveEvent;
import com.github.catageek.ByteCart.Updaters.UpdaterContentFactory;
import com.github.catageek.ByteCart.Wanderer.WandererContentFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.io.IOException;
import java.util.Calendar;

/**
 * Launch an event when an updater moves
 * This listener unregisters itself automatically if there is no updater
 */
public class ByteCartUpdaterMoveListener implements Listener {

    // flag for singleton
    private static boolean exist = false;

    // A map with ephemeral elements and timers
    private static UpdaterSet updaterset = new UpdaterSet();

    /**
     * @return the exist
     */
    public static boolean isExist() {
        return exist;
    }

    /**
     * @param exist the exist to set
     */
    public static void setExist(boolean exist) {
        if (!isExist() && exist) {
            updaterset = new UpdaterSet();
        }
        ByteCartUpdaterMoveListener.exist = exist;
    }

    /**
     * Add a vehicle id in the updater map
     *
     * @param id the vehicle id
     */
    public static final void addUpdater(int id) {
        updaterset.getMap().add(id);
    }

    public static final void clearUpdaters() {
        if (updaterset != null) {
            updaterset.clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleMoveEvent(VehicleMoveEvent event) {

        Location loc = event.getFrom();
        Integer from_x = loc.getBlockX();
        Integer from_z = loc.getBlockZ();
        loc = event.getTo();
        int to_x = loc.getBlockX();
        int to_z = loc.getBlockZ();


        // Check if the vehicle crosses a cube boundary
        if (from_x == to_x && from_z == to_z) {
            return;    // no boundary crossed, resumed
        }

        Vehicle v = event.getVehicle();
        // reset the timer
        if (v instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) v).getInventory();
            if (WandererContentFactory.isWanderer(inv, "Updater")) {
                Bukkit.getServer().getPluginManager().callEvent((Event) new UpdaterMoveEvent(event));
                try {
                    long duration = UpdaterContentFactory.getUpdaterContent(inv).getExpirationTime()
                            - Calendar.getInstance().getTimeInMillis();
                    if (duration < 1000) {
                        updaterset.getMap().reset(duration / 50, v.getEntityId());
                        Bukkit.getServer().getPluginManager().callEvent((Event) new UpdaterRemoveEvent(v.getEntityId()));
                    }
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }
        }

        if (updaterset.getMap().isEmpty()) {
            removeListener();
        }
    }

    /**
     * Detect a destroyed updater
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle v = event.getVehicle();
        if (v instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) v).getInventory();
            if (WandererContentFactory.isWanderer(inv, "Updater")) {
                Bukkit.getServer().getPluginManager().callEvent((Event) new UpdaterRemoveEvent(v.getEntityId()));
            }
        }
    }

    private void removeListener() {
        HandlerList.unregisterAll(this);
        updaterset = null;
        setExist(false);
    }
}
