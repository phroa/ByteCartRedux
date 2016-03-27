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

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.address.ReturnAddressFactory;
import com.github.catageek.bytecart.ByteCartRedux;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * A return address setter
 */
final class BC7015 extends BC7011 implements Triggerable {

    BC7015(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    protected AddressRouted getTargetAddress() {
        return ReturnAddressFactory.getAddress(this.getInventory());
    }

    @Override
    protected final boolean getIsTrain() {
        Address address;
        if ((address = AddressFactory.getAddress(this.getInventory())) != null) {
            return address.isTrain();
        }
        return false;
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
        ((Player) this.getInventory().getHolder()).sendMessage(
                ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + ByteCartRedux.rootNode.getNode("Info", "SetReturnAddress").getString() + " "
                        + ChatColor.RED + address);
    }
}
