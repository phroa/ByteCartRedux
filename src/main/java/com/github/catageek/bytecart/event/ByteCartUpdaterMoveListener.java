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

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.event.custom.UpdaterMoveEvent;
import com.github.catageek.bytecart.event.custom.UpdaterRemoveEvent;
import com.github.catageek.bytecart.updater.UpdaterContentFactory;
import com.github.catageek.bytecart.updater.UpdaterSet;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

/**
 * Launch an event when an updater moves
 * This listener unregisters itself automatically if there is no updater
 */
public class ByteCartUpdaterMoveListener implements EventListener<DisplaceEntityEvent.Move> {

    // flag for singleton
    private static boolean exist = false;

    // A map with ephemeral elements and timers
    private static UpdaterSet updaterSet = new UpdaterSet();

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
            updaterSet = new UpdaterSet();
        }
        ByteCartUpdaterMoveListener.exist = exist;
    }

    /**
     * Add a vehicle id in the updater map
     *
     * @param id the vehicle id
     */
    public static void addUpdater(UUID id) {
        updaterSet.getMap().add(id);
    }

    public static void clearUpdaters() {
        if (updaterSet != null) {
            updaterSet.clear();
        }
    }

    @Listener
    @Override
    public void handle(DisplaceEntityEvent.Move event) {

        Location<World> loc = event.getFromTransform().getLocation();
        Integer fromX = loc.getBlockX();
        Integer fromZ = loc.getBlockZ();
        loc = event.getToTransform().getLocation();
        int toX = loc.getBlockX();
        int toZ = loc.getBlockZ();


        // Check if the vehicle crosses a cube boundary
        if (fromX == toX && fromZ == toZ) {
            return;    // no boundary crossed, resumed
        }

        Entity v = event.getTargetEntity();
        // reset the timer
        if (v instanceof Carrier) {
            CarriedInventory inv = ((Carrier) v).getInventory();
            if (WandererContentFactory.isWanderer(inv, "Updater")) {
                Sponge.getEventManager().registerListeners(ByteCartRedux.myPlugin, new ByteCartUpdaterDestructEvent());
                Sponge.getEventManager().post(new UpdaterMoveEvent(event));
                try {
                    long duration = UpdaterContentFactory.getUpdaterContent(inv).getExpirationTime()
                            - Calendar.getInstance().getTimeInMillis();
                    if (duration < 1000) {
                        updaterSet.getMap().reset(duration / 50, v.getUniqueId());
                        Sponge.getEventManager().post(new UpdaterRemoveEvent(v.getUniqueId()));
                    }
                } catch (ClassNotFoundException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }
        }

        if (updaterSet.getMap().isEmpty()) {
            removeListener();
        }
    }

    private void removeListener() {
        Sponge.getEventManager().unregisterListeners(this);
        updaterSet = null;
        setExist(false);
    }
}
