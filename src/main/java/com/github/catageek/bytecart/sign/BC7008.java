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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.vehicle.minecart.ContainerMinecart;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;

/**
 * A cart remover
 */
final class BC7008 extends AbstractTriggeredSign implements Triggerable {

    /**
     * @param block
     */
    public BC7008(BlockSnapshot block, Entity vehicle) {
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
        Entity vehicle = this.getVehicle();

        // we eject the passenger
        if (vehicle.getPassenger() != null) {
            vehicle.setPassenger(null);
        }

        // we drop items
        if (ByteCartRedux.myPlugin.keepItems()) {
            Inventory inventory;
            if (vehicle instanceof ContainerMinecart) {
                inventory = ((ContainerMinecart) vehicle).getInventory();
                World world = this.getBlock().getLocation().get().getExtent();
                Location<World> loc = this.getBlock().getLocation().get().add(Direction.UP.toVector3d().mul(2));
                inventory.forEach(stack -> world
                        .spawnEntity(world.createEntity(EntityTypes.ITEM, loc.getPosition()).get(), Cause.builder().owner(vehicle).build()));
            }
        }

        vehicle.remove();
    }
}
