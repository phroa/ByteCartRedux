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

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.hardware.RegistryBoth;

/**
 * A class implementing an address using a book as support
 */
final class AddressBook implements AddressRouted {

    /**
     * The ticket used as support for the address
     */
    private final Ticket ticket;
    /**
     * The type of the address
     */
    private final Parameter parameter;

    /**
     * Creates an address using a ticket and a parameter
     *
     * @param ticket the ticket where to store the address
     * @param parameter the type of address to store
     */
    AddressBook(Ticket ticket, Parameter parameter) {
        this.ticket = ticket;
        this.parameter = parameter;
    }

    @Override
    public RegistryBoth getRegion() {
        return getAddress().getRegion();
    }

    @Override
    public RegistryBoth getTrack() {
        return getAddress().getTrack();
    }

    @Override
    public RegistryBoth getStation() {
        return getAddress().getStation();
    }

    @Override
    public boolean isTrain() {
        return ticket.getString(Parameter.TRAIN, "false").equalsIgnoreCase("true");
    }

    @Override
    public boolean setAddress(String s) {
        return setAddress(s, null);
    }

    @Override
    public boolean setAddress(String value, String stationname) {
        boolean ret = this.ticket.setEntry(parameter, value);

        if (parameter.equals(Parameter.DESTINATION)) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.myPlugin.getLog().info("set title");
            }
            ticket.appendTitle(stationname, value);
        }
        return ret;
    }

    @Override
    public boolean setTrain(boolean istrain) {
        if (istrain) {
            return ticket.setEntry(Parameter.TRAIN, "true");
        }
        ticket.remove(Parameter.TRAIN);
        return true;
    }

    @Override
    public boolean isValid() {
        return getAddress().isValid();
    }

    @Override
    public int getTTL() {
        return ticket.getInt(Parameter.TTL, ByteCartRedux.rootNode.getNode("book", "ttl").getInt());
    }

    @Override
    public void updateTTL(int i) {
        ticket.setEntry(Parameter.TTL, "" + i);
    }

    @Override
    public void initializeTTL() {
        this.ticket.resetValue(Parameter.TTL, "64");
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }

    /**
     * Read the address from the ticket
     *
     * @return the address
     */
    private Address getAddress() {
        String defaultaddr = ByteCartRedux.rootNode.getNode("defaultroute", "empty").getString("0.0.0");
        return new AddressString(ticket.getString(parameter, defaultaddr), true);
    }

    @Override
    public void remove() {
        ticket.remove(parameter);
    }

    @Override
    public boolean isReturnable() {
        return ticket.getString(Parameter.RETURN) != null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finalizeAddress() {
        ticket.close();
    }

    /**
     * Parameters name used in the book
     */
    public enum Parameter {
        DESTINATION("net.dst.addr"),
        RETURN("net.src.addr"),
        TTL("net.ttl"),
        TRAIN("net.train");

        private final String name;

        Parameter(String s) {
            name = s;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
    }
}
