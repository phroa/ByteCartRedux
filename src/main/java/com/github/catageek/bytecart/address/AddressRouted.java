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


/**
 * Represents an address currently routed
 */
public interface AddressRouted extends Address {

    /**
     * Get the TTL (time-to-live) associated with the address
     *
     *
     * @return the TTL
     */
    int getTTL();

    /**
     * Set the TTL
     *
     * {@link Address#finalizeAddress()} should be called later to actually set the TTL
     *
     * @param i the value to set
     */
    void updateTTL(int i);

    /**
     * Initialize TTL to its default value
     *
     * {@link Address#finalizeAddress()} should be called later to actually initialize the TTL
     *
     */
    void initializeTTL();
}
