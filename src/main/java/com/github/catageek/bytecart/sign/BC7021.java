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
 * Power the lever on the train head but not on wagons
 */
final class BC7021 extends BC7020 implements Triggerable {

    BC7021(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    protected void actionWagon() {
        this.getOutput(0).setAmount(0);    // deactivate levers
    }

    @Override
    public final String getName() {
        return "BC7021";
    }

    @Override
    public final String getFriendlyName() {
        return "Is Train Head";
    }

}
