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
import com.github.catageek.bytecart.hardware.RegistryInput;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

/**
 * A region field setter using redstone
 */
final class BC7012 extends BC7013 implements Triggerable {

    BC7012(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    protected String format(RegistryInput wire, AddressRouted InvAddress) {
        return "" + wire.getValue() + "."
                + InvAddress.getTrack().getValue() + "."
                + InvAddress.getStation().getValue();
    }

    @Override
    public final String getName() {
        return "BC7012";
    }

    @Override
    public final String getFriendlyName() {
        return "Set Region";
    }

    @Override
    protected boolean forceTicketReuse() {
        return true;
    }

}
