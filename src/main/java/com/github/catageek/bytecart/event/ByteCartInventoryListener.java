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
import com.github.catageek.bytecart.ModifiableRunnable;
import com.github.catageek.bytecart.event.custom.UpdaterCreateEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.ContainerMinecart;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

/**
 * Class implementing a listener and waiting for a player to right-click an inventory holder
 * and running a Runnable
 */
public class ByteCartInventoryListener<T extends Inventory> implements EventListener<InteractEntityEvent.Secondary> {

    private final Player player;
    // the Runnable to update
    private final ModifiableRunnable<T> execute;
    // flag set when we deal with an updater command
    private final boolean isUpdater;

    public ByteCartInventoryListener(ByteCartRedux plugin, Player player, ModifiableRunnable<T> execute,
            boolean isUpdater) {
        this.player = player;
        this.execute = execute;
        this.isUpdater = isUpdater;
        // self registering as Listener
        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    @Override
    public void handle(InteractEntityEvent.Secondary event) {
        if (event.getCause().containsType(Player.class)) {
            return;
        }
        Player player = event.getCause().first(Player.class).get();
        Entity entity;
        T inv;
        if (player.equals(this.player) && ((entity = event.getTargetEntity()) instanceof Carrier)) {
            // we set the member and run the Runnable
            this.execute.setParam(inv = (T) ((Carrier) entity).getInventory());
            this.execute.run();
            // we cancel the right-click
            event.setCancelled(true);

            if (isUpdater) {
                // we launch an UpdaterCreateEvent
                ContainerMinecart v = ((CarriedInventory<ContainerMinecart>) inv).getCarrier().get();
                UpdaterCreateEvent e = new UpdaterCreateEvent(v.getUniqueId(), v.getLocation());
                Sponge.getEventManager().post(e);
            }
        }
        // Self unregistering
        Sponge.getEventManager().unregisterListeners(this);
    }
}


