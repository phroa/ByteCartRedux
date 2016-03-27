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

import java.io.IOException;

/**
 * An eject sign
 */
final class BC7005 extends AbstractTriggeredSign implements Triggerable {

    /**
     * @param block
     * @param vehicle
     */
    public BC7005(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        if (this.getVehicle() != null) {
            this.getVehicle().eject();
        }
    }

    @Override
    public String getName() {
        return "BC7005";
    }

    @Override
    public String getFriendlyName() {
        return "Eject";
    }

}
