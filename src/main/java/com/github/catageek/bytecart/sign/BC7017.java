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
import com.github.catageek.bytecart.address.ReturnAddressFactory;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.text.Text;

/**
 * A block that makes the cart return to its origin using return address
 */
public final class BC7017 extends AbstractTriggeredSign implements Triggerable {

    public BC7017(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    public BC7017(BlockSnapshot block, Player player) {
        super(block, null);
        this.setInventory(player.getInventory());
    }

    @Override
    public String getName() {
        return "BC7017";
    }

    @Override
    public String getFriendlyName() {
        return "Cart Return";
    }

    @Override
    public void trigger() {
        Address returnAddress = ReturnAddressFactory.getAddress(this.getInventory());

        if (returnAddress == null || !returnAddress.isReturnable()) {
            return;
        }

        String returnAddressString = returnAddress.toString();
        AddressRouted targetAddress = AddressFactory.getAddress(getInventory());
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("7017 : Writing address " + returnAddressString);
        }
        returnAddress.remove();
        returnAddress.finalizeAddress();
        boolean isTrain = targetAddress.isTrain();
        targetAddress.setAddress(returnAddressString);
        targetAddress.setTrain(isTrain);
        if (this.getInventory() instanceof HumanInventory) {
            Messaging.sendSuccess(((Player) ((HumanInventory) this.getInventory()).getCarrier().get()),
                    Text.of(String.format(ByteCartRedux.rootNode.getNode("messages", "info", "setaddress").getString(), returnAddressString)));
        }
        targetAddress.initializeTTL();
        targetAddress.finalizeAddress();

    }
}
