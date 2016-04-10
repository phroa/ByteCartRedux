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

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressString;
import com.github.catageek.bytecart.address.TicketFactory;
import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;

/**
 * A cart spawner
 */
final class BC7004 extends AbstractIC implements Powerable {

    private final String type;
    private final String address;

    public BC7004(BlockSnapshot block, String type, String address) {
        super(block);
        this.type = type;
        this.address = address;
    }

    @Override
    public void power() throws ClassNotFoundException, IOException {
        BlockSnapshot block = this.getBlock();
        // check if we are really powered
        if (!block.getLocation().get().getRelative(MathUtil.clockwise(getCardinal())).createSnapshot().get(Keys.POWERED).orElse(false)
                && block.getLocation().get().getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot().get(Keys.POWERED).orElse(false)) {
            return;
        }

        // add input command = redstone

        InputPin[] wire = new InputPin[2];

        // Right
        wire[0] = InputPinFactory
                .getInput(block.getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.clockwise(getCardinal())).createSnapshot());
        // left
        wire[1] = InputPinFactory
                .getInput(block.getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot());

        // InputRegistry[0] = wire
        this.addInputRegistry(new PinRegistry<>(wire));

        // if wire is on, we spawn a cart
        if (this.getInput(0).getValue() != 0) {
            BlockSnapshot rail = block.getLocation().get().add(Direction.UP.toVector3d().mul(2)).createSnapshot();
            Location<World> loc = rail.getLocation().get();
            // check that it is a track, and no cart is there
            if (rail.getState().getType().equals(BlockTypes.RAIL) && MathUtil.getVehicleByLocation(loc) == null) {
                Entity entity = block.getLocation().get().getExtent().createEntity(getType(), loc.getPosition()).get();//(loc, getType());
                // put a ticket in the inventory if necessary
                if (entity.getProperty(InventoryProperty.class).isPresent() && AddressString.isAddress(address)) {
                    CarriedInventory<?> inv = ((CarriedInventory<?>) entity.getProperty(InventoryProperty.class).get());
                    TicketFactory.getOrCreateTicket(inv);
                    Address dst = AddressFactory.getAddress(inv);
                    dst.setAddress(address);
                    dst.finalizeAddress();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "BC7004";
    }

    @Override
    public String getFriendlyName() {
        return "Cart Spawner";
    }

    /**
     * Get the type of cart to spawn
     *
     * @return the type
     */
    private EntityType getType() {
        if (type.equalsIgnoreCase("storage")) {
            return EntityTypes.CHESTED_MINECART;
        }
        if (type.equalsIgnoreCase("furnace")) {
            return EntityTypes.FURNACE_MINECART;
        }
        if (type.equalsIgnoreCase("hopper")) {
            return EntityTypes.HOPPER_MINECART;
        }

        return EntityTypes.RIDEABLE_MINECART;
    }
}
