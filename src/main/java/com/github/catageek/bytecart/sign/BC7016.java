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
import com.github.catageek.bytecart.address.ReturnAddressFactory;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

/**
 * A return address checker
 */
final class BC7016 extends AbstractTriggeredSign implements Triggerable {

    BC7016(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    public String getName() {
        return "BC7016";
    }

    @Override
    public String getFriendlyName() {
        return "Is Returnable";
    }

    @Override
    public void trigger() {
        addIO();
        Address returnAddress = ReturnAddressFactory.getAddress(this.getInventory());

        if (returnAddress != null && returnAddress.isReturnable()) {
            this.getOutput(0).setAmount(3);
        } else {
            this.getOutput(0).setAmount(0);
        }
    }

    /**
     * Register the levers output
     *
     */
    private void addIO() {
        // Output[0] = 2 bits registry representing levers on the left and on the right of the sign
        OutputPin[] lever2 = new OutputPin[2];

        // Left
        lever2[0] = OutputPinFactory
                .getOutput(this.getBlock().getLocation().get().getRelative(MathUtil.anticlockwise(this.getCardinal())).createSnapshot());
        // Right
        lever2[1] =
                OutputPinFactory.getOutput(this.getBlock().getLocation().get().getRelative(MathUtil.clockwise(this.getCardinal())).createSnapshot());

        PinRegistry<OutputPin> command1 = new PinRegistry<>(lever2);

        this.addOutputRegistry(command1);
    }

}
