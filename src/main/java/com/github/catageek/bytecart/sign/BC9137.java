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
 * Match IP ranges and negate the result.
 *
 * 1. Empty
 * 2. [BC9137]
 * 3. AA.BB.CC
 * 4. XX.YY.ZZ
 *
 * onState <=> !(AA.BB.CC <= IP <= XX.YY.ZZ)
 */
final class BC9137 extends AbstractBC9037 {

    BC9137(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    protected boolean negated() {
        return true;
    }

    @Override
    public final String getName() {
        return "BC9137";
    }

    @Override
    public final String getFriendlyName() {
        return "Negated Range Matcher";
    }

    @Override
    public boolean isLeverReversed() {
        return true;
    }
}
