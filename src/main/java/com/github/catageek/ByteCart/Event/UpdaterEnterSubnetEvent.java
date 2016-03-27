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
package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a local updater enters a subnet
 */
public class UpdaterEnterSubnetEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address address, oldaddress;
    private final int length, oldlength;

    /**
     * Default constructor
     *
     * @param updater updater involved
     * @param address address of the subnet
     * @param length number of stations this subnet can contain
     * @param oldlength number of stations the subnet we are nested in can contain
     * @param oldaddress address of the subnet we are nested in
     */
    public UpdaterEnterSubnetEvent(Wanderer updater, Address address, int length, Address oldaddress, int oldlength) {
        super(updater);
        this.address = address;
        this.length = length;
        this.oldaddress = oldaddress;
        this.oldlength = oldlength;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the length of the subnet we enter
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the address of the subnet we enter
     */
    public String getAddress() {
        return address.toString();
    }

    /**
     * @return the address of the subnet we are nested in
     */
    public String getOldAddressString() {
        return oldaddress.toString();
    }

    /**
     * @return the address of the subnet we are nested in
     */
    @Deprecated
    public Address getOldAddress() {
        return oldaddress;
    }

    /**
     * @return the length of the subnet we are nested in
     */
    public int getOldLength() {
        return oldlength;
    }
}
