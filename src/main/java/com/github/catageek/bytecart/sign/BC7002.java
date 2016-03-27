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
import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * A cart detector
 */
final class BC7002 extends AbstractTriggeredSign implements Triggerable {

    BC7002(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() {
        OutputPin[] lever = new OutputPin[1];

        // Right
        lever[0] = OutputPinFactory.getOutput(this.getBlock().getRelative(getCardinal()));

        // OutputRegistry[1] = red light signal
        this.addOutputRegistry(new PinRegistry<OutputPin>(lever));

        this.getOutput(0).setAmount(1);
        (new Release(this)).runTaskLater(ByteCartRedux.myPlugin, 4);

    }

    @Override
    public final String getName() {
        return "BC7002";
    }

    @Override
    public final String getFriendlyName() {
        return "Cart detector";
    }

    private final class Release extends BukkitRunnable {

        private final AbstractIC bc;

        public Release(AbstractIC bc) {
            this.bc = bc;
        }

        @Override
        public void run() {
            this.bc.getOutput(0).setAmount(0);
        }
    }
}