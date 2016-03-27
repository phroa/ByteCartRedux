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
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.AddressLayer.AddressFactory;
import com.github.catageek.ByteCart.AddressLayer.AddressRouted;
import com.github.catageek.ByteCart.AddressLayer.TicketFactory;
import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.HAL.AbstractIC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

/**
 * Base class for all signs that are triggered by vehicles that pass over it.
 *
 * Reads the address configuration of the vehicle from its inventory and caches it.
 */
abstract class AbstractTriggeredSign extends AbstractIC implements Triggable {

    private final org.bukkit.entity.Vehicle Vehicle;
    private org.bukkit.inventory.Inventory Inventory;

    AbstractTriggeredSign(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block);
        this.Vehicle = vehicle;

        this.Inventory = this.extractInventory();
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
    final public org.bukkit.entity.Vehicle getVehicle() {
        return Vehicle;
    }

    /**
     * Extract the address configuration of the current vehicle. If the vehicle has
     * no address configuration, the default address (if configured) is applied.
     *
     * @return Inventory with address configuration from the current vehicle.
     */
    final private org.bukkit.inventory.Inventory extractInventory() {

        org.bukkit.inventory.Inventory newInv = Bukkit.createInventory(null, 27);


        // we load inventory of cart or player
        if (this.Vehicle != null) {

            if (this.getVehicle() instanceof InventoryHolder) {
                return ((InventoryHolder) this.getVehicle()).getInventory();
            } else if (this.getVehicle() instanceof Minecart) {
                if (!this.getVehicle().isEmpty()) {
                    if (((Minecart) this.getVehicle()).getPassenger() instanceof Player) {

                        if (ByteCartRedux.debug) {
                            ByteCartRedux.log.info("ByteCartRedux: loading player inventory :" + ((Player) this.getVehicle().getPassenger()).getDisplayName());
                        }

                        return ((Player) this.getVehicle().getPassenger()).getInventory();
                    }
                }
            }

			/* There is no inventory, so we create one */

            // we have a default route ? so we write it in inventory
            if (ByteCartRedux.myPlugin.getConfig().contains("EmptyCartsDefaultRoute")) {
                String DefaultRoute = ByteCartRedux.myPlugin.getConfig().getString("EmptyCartsDefaultRoute");
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
    public org.bukkit.inventory.Inventory getInventory() {
        return Inventory;
    }

    /**
     * Set the inventory variable
     *
     * @param inv
     */
    protected void setInventory(org.bukkit.inventory.Inventory inv) {
        this.Inventory = inv;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.Triggable#isTrain()
     */
    @Override
    public final boolean isTrain() {
        return AbstractTriggeredSign.isTrain(AddressFactory.getAddress(this.getInventory()));
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.Triggable#wasTrain(org.bukkit.Location)
     */
    @Override
    public final boolean wasTrain(Location loc) {
        boolean ret;
        if (ByteCartRedux.myPlugin.getIsTrainManager().getMap().contains(loc)) {
            ret = ByteCartRedux.myPlugin.getIsTrainManager().getMap().get(loc);
            /*			if(ByteCartRedux.debug  && ret)
				ByteCartRedux.log.info("ByteCartRedux: "+ this.getName() + " at " + this.getLocation() + " : " + this.getVehicle() + " is wagon !");
			 */
            return ret;
        }
		/*		if(ByteCartRedux.debug)
			ByteCartRedux.log.info("ByteCartRedux: "+ this.getName() + " at " + this.getLocation() + " : " + this.getVehicle() + " is not wagon !");
		 */
        return false;
    }

    /**
     * Remember the train bit
     *
     * @param loc the location where to store the bit
     * @param b the bit
     */
    protected final void setWasTrain(Location loc, boolean b) {
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
