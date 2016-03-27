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
import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.HAL.RegistryInput;
import com.github.catageek.ByteCart.IO.InputFactory;
import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.block.BlockFace;

/**
 * A station field setter using a redstone signal strength
 */
class BC7014 extends BC7010 implements Triggable {

    BC7014(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        this.StorageCartAllowed = true;
    }

    @Override
    protected Address getAddressToWrite() {
        addIO();
        AddressRouted InvAddress = AddressFactory.getAddress(this.getInventory());

        if (InvAddress == null) {
            return null;
        }

        RegistryInput wire = this.getInput(0);

        if (wire == null || wire.getAmount() == 0) {
            return null;
        }

        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux: " + this.getName() + " wire : " + wire.getAmount());
        }

        return AddressFactory.getAddress(format(wire, InvAddress));
    }

    /**
     * Build the address string
     *
     * @param wire the wire to take as input
     * @param InvAddress the address to modify
     * @return a string containing the address
     */
    protected String format(RegistryInput wire, AddressRouted InvAddress) {
        return "" + InvAddress.getRegion().getAmount() + "."
                + InvAddress.getTrack().getAmount() + "."
                + wire.getAmount();
    }

    /**
     * Register the input wire on the left of the sign
     *
     */
    protected void addIO() {
        // Input[0] : wire on left
        org.bukkit.block.Block block = this.getBlock().getRelative(BlockFace.UP).getRelative(MathUtil.anticlockwise(getCardinal()));
        RegistryInput wire = InputFactory.getInput(block);
        this.addInputRegistry(wire);
    }

    @Override
    protected final boolean getIsTrain() {
        boolean signtrain = super.getIsTrain();
        Address address;
        if ((address = AddressFactory.getAddress(this.getInventory())) != null) {
            return address.isTrain() || signtrain;
        }
        return signtrain;
    }

    @Override
    public String getName() {
        return "BC7014";
    }

    @Override
    public String getFriendlyName() {
        return "setStation";
    }

    @Override
    protected boolean forceTicketReuse() {
        return true;
    }
}
