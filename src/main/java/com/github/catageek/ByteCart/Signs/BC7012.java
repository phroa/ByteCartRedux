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

import com.github.catageek.ByteCart.AddressLayer.AddressRouted;
import com.github.catageek.ByteCart.HAL.RegistryInput;

/**
 * A region field setter using redstone
 */
final class BC7012 extends BC7013 implements Triggable {

    BC7012(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7013#format(com.github.catageek.ByteCartRedux.HAL.RegistryInput, com.github.catageek.ByteCartRedux
     * .AddressLayer.AddressRouted)
     */
    @Override
    protected String format(RegistryInput wire, AddressRouted InvAddress) {
        return "" + wire.getAmount() + "."
                + InvAddress.getTrack().getAmount() + "."
                + InvAddress.getStation().getAmount();
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7013#getName()
     */
    @Override
    public final String getName() {
        return "BC7012";
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7013#getFriendlyName()
     */
    @Override
    public final String getFriendlyName() {
        return "setRegion";
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.BC7013#forceTicketReuse()
     */
    @Override
    protected boolean forceTicketReuse() {
        return true;
    }

}
