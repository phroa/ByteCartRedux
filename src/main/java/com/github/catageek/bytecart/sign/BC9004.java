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


import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

/**
 * A 4-station subnet bound
 */
final class BC9004 extends AbstractBC9000 implements Subnet, HasNetmask, Triggerable {

    BC9004(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        this.netmask = 6;
    }

    @Override
    public final String getName() {
        return "BC9004";
    }

    @Override
    public final String getFriendlyName() {
        return "4-station subnet";
    }
}
