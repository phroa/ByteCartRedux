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

import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.hardware.RegistryInput;
import com.github.catageek.bytecart.hardware.SuperRegistry;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

/**
 * A ring field setter using redstone
 */
class BC7013 extends BC7014 implements Triggerable {

    BC7013(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    protected String format(RegistryInput wire, AddressRouted InvAddress) {
        return "" + InvAddress.getRegion().getValue() + "."
                + wire.getValue() + "."
                + InvAddress.getStation().getValue();
    }

    @Override
    protected void addIO() {
        // Input[0] : wire on left
        BlockSnapshot block = this.getBlock().getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot();
        RegistryInput wire = InputPinFactory.getInput(block);

        InputPin[] levers = new InputPin[2];
        block = this.getBlock().getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.clockwise(getCardinal())).createSnapshot();
        levers[0] = InputPinFactory.getInput(block);

        block = this.getBlock().getLocation().get().getRelative(getCardinal().getOpposite()).createSnapshot();
        levers[1] = InputPinFactory.getInput(block);

        RegistryInput ret = new SuperRegistry<>(new PinRegistry<>(levers), wire);

        this.addInputRegistry(ret);

    }

    @Override
    public String getName() {
        return "BC7013";
    }

    @Override
    public String getFriendlyName() {
        return "setTrack";
    }

    @Override
    protected boolean forceTicketReuse() {
        return true;
    }
}
