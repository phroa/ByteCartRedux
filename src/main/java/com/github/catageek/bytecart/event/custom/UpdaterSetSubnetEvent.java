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
package com.github.catageek.bytecart.event.custom;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.updater.Wanderer;

/**
 * Event triggered when a local updater modifies the address
 * of a BC9XXX sign, except BC9001.
 */
public class UpdaterSetSubnetEvent extends UpdaterClearSubnetEvent {

    private final Address newAddress;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the subnet
     * @param newAddress The new address of the subnet
     * @param length number of stations this subnet can contain
     */
    public UpdaterSetSubnetEvent(Wanderer updater, Address oldAddress, Address newAddress, int length) {
        super(updater, oldAddress, length);
        this.newAddress = newAddress;
    }

    /**
     * @return the newAddress
     */
    public String getNewAddress() {
        return newAddress.toString();
    }
}
