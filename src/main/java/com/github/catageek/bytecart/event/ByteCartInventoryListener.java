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
import com.github.catageek.bytecart.event.custom.UpdaterCreateEvent;
import com.github.catageek.bytecart.ModifiableRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Class implementing a listener and waiting for a player to right-click an inventory holder
 * and running a Runnable
 */
public class ByteCartInventoryListener implements Listener {

    private final Player Player;
    // the Runnable to update
    private final ModifiableRunnable<Inventory> Execute;
    // flag set when we deal with an updater command
    private final boolean isUpdater;

    public ByteCartInventoryListener(ByteCartRedux plugin, Player player, ModifiableRunnable<Inventory> execute,
            boolean isupdater) {
        this.Player = player;
        this.Execute = execute;
        this.isUpdater = isupdater;
        // self registering as Listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity;
        Inventory inv;
        if (event.getPlayer().equals(Player) && ((entity = event.getRightClicked()) instanceof InventoryHolder)) {
            // we set the member and run the Runnable
            this.Execute.SetParam(inv = ((InventoryHolder) entity).getInventory());
            this.Execute.run();
            // we cancel the right-click
            event.setCancelled(true);

            if (isUpdater) {
                // we launch an UpdaterCreateEvent
                StorageMinecart v = (StorageMinecart) inv.getHolder();
                UpdaterCreateEvent e = new UpdaterCreateEvent(v.getEntityId(), v.getLocation());
                ByteCartRedux.myPlugin.getServer().getPluginManager().callEvent(e);
            }
        }
        // Self unregistering
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
    }
}


