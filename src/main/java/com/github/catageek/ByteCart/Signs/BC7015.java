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
import com.github.catageek.ByteCart.AddressLayer.ReturnAddressFactory;
import com.github.catageek.ByteCart.ByteCartRedux;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * A return address setter
 */
final class BC7015 extends BC7011 implements Triggable {

    BC7015(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7010#getTargetAddress()
     */
    @Override
    protected AddressRouted getTargetAddress() {
        return ReturnAddressFactory.getAddress(this.getInventory());
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7010#getIsTrain()
     */
    @Override
    protected final boolean getIsTrain() {
        Address address;
        if ((address = AddressFactory.getAddress(this.getInventory())) != null) {
            return address.isTrain();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7011#getName()
     */
    @Override
    public String getName() {
        return "BC7015";
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7011#getFriendlyName()
     */
    @Override
    public String getFriendlyName() {
        return "Set Return";
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7010#forceTicketReuse()
     */
    @Override
    protected boolean forceTicketReuse() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7010#infoPlayer(com.github.catageek.ByteCartRedux.AddressLayer.Address)
     */
    @Override
    protected void infoPlayer(String address) {
        ((Player) this.getInventory().getHolder()).sendMessage(
                ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + ByteCartRedux.myPlugin.getConfig().getString("Info.SetReturnAddress") + " "
                        + ChatColor.RED + address);
    }
}
