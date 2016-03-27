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
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a local updater leaves a subnet
 */
public class UpdaterLeaveSubnetEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address address, newaddress;
    private final int length, newlength;

    /**
     * Default constructor
     *
     * @param updater updater involved
     * @param address address of the subnet we are leaving
     * @param length number of stations this subnet can contain
     * @param newlength number of stations of the subnet we are re-entering
     * @param newaddress address of the subnet we are re-entering
     */
    public UpdaterLeaveSubnetEvent(Wanderer updater, Address address, int length, Address newaddress, int newlength) {
        super(updater);
        this.address = address;
        this.length = length;
        this.newaddress = newaddress;
        this.newlength = newlength;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the length of the old subnet
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the address of the old subnet
     */
    public String getAddress() {
        return address.toString();
    }

    /**
     * @return the address of the new subnet
     */
    @Deprecated
    public Address getNewaddress() {
        return newaddress;
    }

    /**
     * @return the address of the new subnet
     */
    public String getNewAddress() {
        return newaddress.toString();
    }

    /**
     * @return the length of the new subnet
     */
    public int getNewlength() {
        return newlength;
    }
}
