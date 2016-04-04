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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A return address setter
 */
final class BC7015 extends BC7011 implements Triggerable {

    BC7015(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    protected AddressRouted getTargetAddress() {
        return ReturnAddressFactory.getAddress(this.getInventory());
    }

    @Override
    protected final boolean getIsTrain() {
        Address address;
        return (address = AddressFactory.getAddress(this.getInventory())) != null && address.isTrain();
    }

    @Override
    public String getName() {
        return "BC7015";
    }

    @Override
    public String getFriendlyName() {
        return "Set Return";
    }

    @Override
    protected boolean forceTicketReuse() {
        return true;
    }

    @Override
    protected void infoPlayer(String address) {
        ((Player) ((HumanInventory) this.getInventory()).getCarrier().get()).sendMessage(
                Text.builder().color(TextColors.DARK_GREEN).append(Text.of("[Bytecart] ")).color(TextColors.YELLOW)
                        .append(Text.of(ByteCartRedux.rootNode.getNode("Info", "SetReturnAddress").getString() + " ")).color(TextColors.RED)
                        .append(Text.of(address)).build());
    }
}
