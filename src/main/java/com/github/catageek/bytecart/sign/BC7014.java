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
import com.github.catageek.bytecart.hardware.RegistryInput;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

/**
 * A station field setter using a redstone signal strength
 */
class BC7014 extends BC7010 implements Triggerable {

    BC7014(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        this.storageCartAllowed = true;
    }

    @Override
    protected Address getAddressToWrite() {
        addIO();
        AddressRouted InvAddress = AddressFactory.getAddress(this.getInventory());

        if (InvAddress == null) {
            return null;
        }

        RegistryInput wire = this.getInput(0);

        if (wire == null || wire.getValue() == 0) {
            return null;
        }

        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux: " + this.getName() + " wire : " + wire.getValue());
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
    String format(RegistryInput wire, AddressRouted InvAddress) {
        return "" + InvAddress.getRegion().getValue() + "."
                + InvAddress.getTrack().getValue() + "."
                + wire.getValue();
    }

    /**
     * Register the input wire on the left of the sign
     *
     */
    void addIO() {
        // Input[0] : wire on left
        BlockSnapshot block = this.getBlock().getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot();
        RegistryInput wire = InputPinFactory.getInput(block);
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
