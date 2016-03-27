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
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.entity.Minecart;

import java.io.IOException;

/**
 * A booster
 */
final class BC7006 extends AbstractTriggeredSign implements Triggable {

    /**
     * @param block
     * @param vehicle
     */
    public BC7006(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        org.bukkit.entity.Vehicle vehicle = this.getVehicle();
        Minecart cart = (Minecart) vehicle;
        if (cart.getMaxSpeed() <= 0.4D) {
            cart.setMaxSpeed(0.68D);
        }

        MathUtil.setSpeed(cart, 0.68D);
    }

    @Override
    public String getName() {
        return "BC7006";
    }

    public String getFriendlyName() {
        return "Booster";
    }
}
