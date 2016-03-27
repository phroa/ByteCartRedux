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
/**
 *
 */
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.ByteCartRedux;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ListIterator;

/**
 * A cart remover
 */
final class BC7008 extends AbstractTriggeredSign implements Triggerable {

    /**
     * @param block
     */
    public BC7008(org.bukkit.block.Block block, Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public String getName() {
        return "BC7008";
    }

    @Override
    public String getFriendlyName() {
        return "Cart remover";
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        org.bukkit.entity.Vehicle vehicle = this.getVehicle();

        // we eject the passenger
        if (vehicle.getPassenger() != null) {
            vehicle.eject();
        }

        // we drop items
        if (ByteCartRedux.myPlugin.keepItems()) {
            org.bukkit.inventory.Inventory inventory;
            if (vehicle instanceof InventoryHolder) {
                inventory = ((InventoryHolder) vehicle).getInventory();
                World world = this.getBlock().getWorld();
                org.bukkit.Location loc = this.getBlock().getRelative(BlockFace.UP, 2).getLocation();
                ListIterator<ItemStack> it = inventory.iterator();
                while (it.hasNext()) {
                    ItemStack stack = it.next();
                    if (stack != null) {
                        world.dropItem(loc, stack);
                    }
                }
            }
        }

        vehicle.remove();
    }
}
