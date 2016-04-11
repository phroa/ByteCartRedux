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
import com.github.catageek.bytecart.io.ComponentSign;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.text.Text;


/**
 * A ticket spawner for players
 */
public class BC7010 extends AbstractTriggeredSign implements Triggerable, Clickable {

    boolean storageCartAllowed = false;

    /**
     * Constructor : !! vehicle can be null !!
     */
    BC7010(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    public BC7010(BlockSnapshot block, Player player) {
        super(block, null);
        this.setInventory(player.getInventory());
    }

    @Override
    public final void trigger() {

        if (!this.isHolderAllowed() || WandererContentFactory.isWanderer(getInventory())) {
            return;
        }

        // if this is a cart in a train
        if (this.wasTrain(this.getLocation())) {
            ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getLocation());
            return;
        }

        Address address = getAddressToWrite();

        if (address == null) {
            return;
        }

        boolean isTrain = getIsTrain();

        this.setAddress(address.toString(), isTrain);

        // if this is the first car of a train
        // we save the state during 2 s
        if (isTrain) {
            this.setWasTrain(this.getLocation(), true);
        }
    }

    /**
     * Provide the train bit value to set
     *
     * @return the train bit value
     */
    boolean getIsTrain() {
        return (new ComponentSign(this.getBlock())).getLine(0).equalsIgnoreCase("train");
    }

    /**
     * Provide the address to set in ticket
     *
     * @return the address to write
     */
    Address getAddressToWrite() {
        return AddressFactory.getAddress(this.getBlock(), 3);
    }

    /**
     * Provide the name to display for the destination
     *
     * @return the name
     */
    private String getNameToWrite() {
        return (new ComponentSign(this.getBlock())).getLine(2);
    }

    /**
     * Get the destination address of an existing ticket
     *
     * @return the destination address
     */
    AddressRouted getTargetAddress() {
        return AddressFactory.getAddress(this.getInventory());
    }

    /**
     * Spawn a ticket in inventory and set the destination address
     * The train bit is not set.
     *
     * @param signAddress the destination address
     * @return true if success, false otherwise
     */
    public final boolean setAddress(String signAddress) {
        return setAddress(signAddress, false);
    }

    /**
     * Spawn a ticket in inventory and set the destination address
     *
     * @param signAddress the destination address
     * @param train true if it is a train head
     * @return true if success, false otherwise
     */
    public final boolean setAddress(String signAddress, boolean train) {
        Player player = null;

        if (this.getInventory() instanceof HumanInventory) {
            player = (Player) ((HumanInventory) this.getInventory()).getCarrier().get();
        }

        if (player == null) {
            TicketFactory.getOrCreateTicket(this.getInventory());
        } else {
            TicketFactory.getOrCreateTicket(player, forceTicketReuse());
        }

        AddressRouted targetAddress = getTargetAddress();

        if (targetAddress == null || !targetAddress.setAddress(signAddress)) {

            if (this.getInventory() instanceof HumanInventory) {
                Messaging.sendError(((Player) ((HumanInventory) this.getInventory()).getCarrier().get()),
                        Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "inventoryspace").getString()));
            }
            return false;
        }
        if (this.getInventory() instanceof HumanInventory) {
            this.infoPlayer(signAddress);
        }
        targetAddress.initializeTTL();

        targetAddress.setTrain(train);

        targetAddress.finalizeAddress();
        return true;
    }

    /**
     * Checks that the requestor is allowed to use this IC
     *
     * @return true if the requestor is allowed
     */
    private boolean isHolderAllowed() {
        return this.getInventory() instanceof HumanInventory || storageCartAllowed;
    }

    /**
     * Send message to player in the chat window
     *
     * @param signAddress the address got by the player
     */
    void infoPlayer(String signAddress) {
        Messaging.sendSuccess(((Player) ((HumanInventory) this.getInventory()).getCarrier().get()),
                Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "setaddress").getString()));
        if (this.getVehicle() == null && !ByteCartRedux.rootNode.getNode("book", "use").getBoolean()) {
            Messaging.sendSuccess(((Player) ((HumanInventory) this.getInventory()).getCarrier().get()),
                    Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "setaddress2").getString()));
        }
    }

    @Override
    public final void click() {
        this.trigger();

    }

    @Override
    public String getName() {
        return "BC7010";
    }

    @Override
    public String getFriendlyName() {
        return "Set Destination";
    }

    /**
     * Tells if we must modify an existing ticket or create a new one
     *
     * @return true if modifying, false to create
     */
    boolean forceTicketReuse() {
        return false;
    }
}
