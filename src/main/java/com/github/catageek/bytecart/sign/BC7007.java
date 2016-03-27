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
/**
 *
 */
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.util.MathUtil;
import org.bukkit.entity.Minecart;

import java.io.IOException;

/**
 * An unbooster
 */
final class BC7007 extends AbstractTriggeredSign implements Triggerable {

    /**
     * @param block
     * @param vehicle
     */
    public BC7007(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        org.bukkit.entity.Vehicle vehicle = this.getVehicle();
        Minecart cart = (Minecart) vehicle;
        if (cart.getMaxSpeed() > 0.4D) {
            cart.setMaxSpeed(0.4D);
        }

        MathUtil.setSpeed(cart, 0.4D);
    }

    @Override
    public String getName() {
        return "BC7007";
    }

    public String getFriendlyName() {
        return "Unbooster";
    }
}