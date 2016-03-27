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

import com.github.catageek.bytecart.address.AddressBook.Parameter;
import com.github.catageek.bytecart.hardware.RegistryBoth;

/**
 * A class implementing a return address in a book
 */
final class ReturnAddressBook implements Returnable {

    /**
     * The book address that this class will proxify
     */
    private final AddressBook address;

    /**
     * Creates a return address from a ticket of a certain type
     *
     * @param ticket the ticket to use as support
     * @param parameter the type of the address
     */
    ReturnAddressBook(Ticket ticket, Parameter parameter) {
        this.address = new AddressBook(ticket, parameter);
    }

    @Override
    public RegistryBoth getRegion() {
        return address.getRegion();
    }

    @Override
    public RegistryBoth getTrack() {
        return address.getTrack();
    }

    @Override
    public RegistryBoth getStation() {
        return address.getStation();
    }

    @Override
    public boolean isTrain() {
        return address.isTrain();
    }

    @Override
    public boolean setAddress(String s) {
        return address.setAddress(s);
    }

    @Override
    public boolean setAddress(String s, String name) {
        return address.setAddress(s);
    }

    @Override
    public boolean setTrain(boolean istrain) {
        return address.setTrain(istrain);
    }

    @Override
    public boolean isValid() {
        return address.isValid();
    }

    @Override
    public void remove() {
        address.remove();
    }

    @Override
    public int getTTL() {
        return address.getTTL();
    }

    @Override
    public void updateTTL(int i) {
        address.updateTTL(i);
    }

    @Override
    public void initializeTTL() {
        address.initializeTTL();
    }

    @Override
    public boolean isReturnable() {
        return address.isReturnable();
    }

    @Override
    public String toString() {
        return address.toString();
    }

    @Override
    public void finalizeAddress() {
        address.finalizeAddress();
    }
}
