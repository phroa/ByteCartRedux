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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.address.TicketFactory;
import com.github.catageek.bytecart.hardware.AbstractIC;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Base class for all signs that are triggered by vehicles that pass over it.
 *
 * Reads the address configuration of the vehicle from its inventory and caches it.
 */
abstract class AbstractTriggeredSign extends AbstractIC implements Triggerable {

    private final Entity vehicle;
    private Inventory inventory;

    AbstractTriggeredSign(BlockSnapshot block, Entity vehicle) {
        super(block);
        this.vehicle = vehicle;

        this.inventory = this.extractInventory();
    }

    public static final boolean isTrain(Address address) {
        if (address != null) {
            return address.isTrain();
        }
        return false;
    }

    /**
     * @return The vehicle which triggered the sign.
     */
    public final Entity getVehicle() {
        return vehicle;
    }

    /**
     * Extract the address configuration of the current vehicle. If the vehicle has
     * no address configuration, the default address (if configured) is applied.
     *
     * @return inventory with address configuration from the current vehicle.
     */
    private final Inventory extractInventory() {

        Inventory newInv = CustomInventory.builder().size(27).build();


        // we load inventory of cart or player
        if (this.vehicle != null) {

            if (this.getVehicle().getProperty(InventoryProperty.class).isPresent()) {
                return (Inventory) this.getVehicle().getProperty(InventoryProperty.class).get().getValue();
            } else if (this.getVehicle() instanceof Minecart) {
                if (!this.getVehicle().getPassenger().isPresent()) {
                    if (this.getVehicle().getPassenger().get() instanceof Player) {

                        if (ByteCartRedux.debug) {
                            ByteCartRedux.log.info("ByteCartRedux: loading player inventory :" + ((Player) this.getVehicle().getPassenger().get())
                                    .get(Keys.DISPLAY_NAME));
                        }

                        return ((Player) this.getVehicle().getPassenger().get()).getInventory();
                    }
                }
            }

            /* There is no inventory, so we create one */

            // we have a default route ? so we write it in inventory
            if (!ByteCartRedux.rootNode.getNode("EmptyCartsDefaultRoute").isVirtual()) {
                String DefaultRoute = ByteCartRedux.rootNode.getNode("EmptyCartsDefaultRoute").getString();
                TicketFactory.getOrCreateTicket(newInv);
                //construct address object
                AddressRouted myAddress = AddressFactory.getAddress(newInv);
                //write address
                myAddress.setAddress(DefaultRoute);
                myAddress.initializeTTL();
                myAddress.finalizeAddress();
            }

        }
        return newInv;
    }

    /**
     * @return The inventory of the vehicle which triggered this sign.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Set the inventory variable
     *
     * @param inv
     */
    protected void setInventory(Inventory inv) {
        this.inventory = inv;
    }

    @Override
    public final boolean isTrain() {
        return AbstractTriggeredSign.isTrain(AddressFactory.getAddress(this.getInventory()));
    }

    @Override
    public final boolean wasTrain(Location loc) {
        boolean ret;
        if (ByteCartRedux.myPlugin.getIsTrainManager().getMap().contains(loc)) {
            ret = ByteCartRedux.myPlugin.getIsTrainManager().getMap().get(loc);
            return ret;
        }
        return false;
    }

    /**
     * Remember the train bit
     *
     * @param loc the location where to store the bit
     * @param b the bit
     */
    protected final void setWasTrain(Location<World> loc, boolean b) {
        if (b) {
            ByteCartRedux.myPlugin.getIsTrainManager().getMap().put(loc, true);
        }

    }

    /**
     * Default is lever not reversed
     * @return false
     */
    public boolean isLeverReversed() {
        return false;
    }


}
