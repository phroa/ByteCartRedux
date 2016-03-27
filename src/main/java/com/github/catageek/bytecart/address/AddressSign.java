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
package com.github.catageek.bytecart.address;

import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.io.AbstractComponent;
import com.github.catageek.bytecart.io.ComponentSign;
import org.bukkit.block.Block;


/**
 * Implements an address using a line of a sign as support
 */
final class AddressSign extends AbstractComponent implements Address {

    /**
     * String used as internal storage
     */
    private final AddressString Address;

    /**
     * Creates the address
     *
     * @param block the sign block containing the address
     * @param ligne the line number containing the address
     */
    AddressSign(Block block, int ligne) {

        super(block);

        this.Address = new AddressString((new ComponentSign(block)).getLine(ligne), false);

    }

    @Override
    public final RegistryBoth getRegion() {
        return Address.getRegion();
    }

    @Override
    public final RegistryBoth getTrack() {
        return Address.getTrack();
    }

    @Override
    public final RegistryBoth getStation() {
        return Address.getStation();
    }


    @Override
    public final boolean setAddress(String s) {
        this.Address.setAddress(s);
        return true;
    }

    @Override
    public boolean setAddress(String s, String name) {
        (new ComponentSign(this.getBlock())).setLine(2, name);
        return setAddress(s);
    }

    @Override
    public boolean isTrain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setTrain(boolean istrain) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void finalizeAddress() {
        (new ComponentSign(this.getBlock())).setLine(3, this.Address.toString());
    }

    @Override
    public boolean isValid() {
        return this.Address.isValid;
    }

    @Override
    public void remove() {
        this.Address.remove();
        (new ComponentSign(this.getBlock())).setLine(3, "");
    }

    @Override
    final public String toString() {
        return Address.toString();
    }

    @Override
    public boolean isReturnable() {
        return false;
    }
}
