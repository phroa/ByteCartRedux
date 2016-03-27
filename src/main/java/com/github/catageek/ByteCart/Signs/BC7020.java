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

import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.HAL.PinRegistry;
import com.github.catageek.ByteCart.IO.OutputPin;
import com.github.catageek.ByteCart.IO.OutputPinFactory;
import com.github.catageek.ByteCart.Util.MathUtil;

/**
 * Power the lever on the train including wagons
 */
class BC7020 extends AbstractTriggeredSign implements Triggable {

    BC7020(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() {
        addIO();

        // if this is a cart in a train
        if (this.wasTrain(this.getLocation())) {
            ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getLocation());
            actionWagon();
            return;
        }

        if (this.isTrain()) {
            this.setWasTrain(this.getLocation(), true);
            this.getOutput(0).setAmount(3);    // activate levers
        } else {
            this.getOutput(0).setAmount(0);    // deactivate levers
        }

    }

    /**
     * A method called on each wagon of the train
     *
     */
    protected void actionWagon() {
    }

    /**
     * Register the output levers
     *
     */
    protected void addIO() {
        // Output[0] = 2 bits registry representing levers on the left and on the right of the sign
        OutputPin[] lever2 = new OutputPin[2];

        // Left
        lever2[0] = OutputPinFactory.getOutput(this.getBlock().getRelative(MathUtil.anticlockwise(this.getCardinal())));
        // Right
        lever2[1] = OutputPinFactory.getOutput(this.getBlock().getRelative(MathUtil.clockwise(this.getCardinal())));

        PinRegistry<OutputPin> command1 = new PinRegistry<OutputPin>(lever2);

        this.addOutputRegistry(command1);
    }

    @Override
    public String getName() {
        return "BC7020";
    }

    @Override
    public String getFriendlyName() {
        return "Is a Train ?";
    }

}
