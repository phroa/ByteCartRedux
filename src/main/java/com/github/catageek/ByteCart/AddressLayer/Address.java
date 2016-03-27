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
package com.github.catageek.ByteCart.AddressLayer;

import com.github.catageek.ByteCart.HAL.RegistryBoth;


/**
 * Represents an address that can be used as destination or return address
 */
public interface Address {

    /**
     * Get the region number
     *
     *
     * @return the region number
     */
    RegistryBoth getRegion();

    /**
     * Get the ring number
     *
     *
     * @return the ring number
     */
    RegistryBoth getTrack();

    /**
     * Get the station number
     *
     *
     * @return the station number
     */
    RegistryBoth getStation();

    /**
     * Get the train flag
     *
     *
     * @return true if the flag is set
     */
    boolean isTrain();

    /**
     * Get the state of the return address
     *
     *
     * @return true if this is a return address
     */
    boolean isReturnable();

    /**
     * Set an address with a name
     *
     * <p>{@link Address#finalizeAddress()} should be called later to actually set the address</p>
     *
     * @param s the address to set in the format aa.bb.cc
     * @param name the name to associate to this address
     * @return true if the address was set
     */
    boolean setAddress(String s, String name);

    /**
     * Set an unresolved address
     *
     * <p>{@link Address#finalizeAddress()} should be called later to actually set the address</p>
     *
     * @param s the address to set in the format aa.bb.cc
     * @return true if the address was set
     */
    boolean setAddress(String s);

    /**
     * Set the train flag
     *
     * <p>{@link Address#finalizeAddress()} should be called later to actually set the flag</p>
     *
     * @param istrain must be true to set the flag
     * @return true if the flag was set
     */
    boolean setTrain(boolean istrain);

    /**
     * Flush the address to its support
     *
     *
     */
    void finalizeAddress();

    /**
     * Tell if an address can be considered as valid or should be discarded
     *
     * <p>For example, an empty field is an invalid address even if "0.0.0" is returned</p>
     *
     *
     * @return true if the address is valid
     */
    boolean isValid();

    /**
     * remove the address
     *
     * <p>{@link Address#finalizeAddress()} should be called later to actually remove the address from the support</p>
     */
    void remove();

    /**
     * Return a printable string for this address
     *
     *
     * @return the address as string
     */
    String toString();
}
