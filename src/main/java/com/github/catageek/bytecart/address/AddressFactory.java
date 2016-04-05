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
import com.github.catageek.bytecart.address.AddressBook.Parameter;
import com.github.catageek.bytecart.file.BookFile;
import com.github.catageek.bytecart.file.BookProperties.Conf;
import com.github.catageek.bytecart.sign.BC7010;
import com.github.catageek.bytecart.sign.BC7011;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.ContainerMinecart;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

/**
 * Factory to create address using various supports
 */
public class AddressFactory {

    /**
     * Get an address with a ticket as support
     *
     * @param inv the inventory containing the ticket
     * @return the address or null if there is no ticket
     */
    @SuppressWarnings("unchecked")
    public static <T extends Address> T getAddress(CarriedInventory<?> inv) {
        int slot;
        if ((slot = Ticket.getTicketslot(inv)) != -1) {
            return (T) new AddressBook(new Ticket(new BookFile(inv, slot, false, "ticket"), Conf.NETWORK), Parameter.DESTINATION);
        }
        return null;
    }

    /**
     * Creates a ticket with default address
     *
     * @param inv the inventory containing the ticket
     * @return the address
     */
    @SuppressWarnings("unchecked")
    public static <T extends Address> T getDefaultTicket(CarriedInventory<?> inv) {
        String destination;
        if (inv.getCarrier().get() instanceof Player) {
            destination = ByteCartRedux.rootNode.getNode("defaultroute", "player").getString("0.0.0");
            if ((new BC7010(null, (Player) inv.getCarrier().get())).setAddress(destination)) {
                return (T) new AddressBook(new Ticket(new BookFile(inv, Ticket.getTicketslot(inv), false, "ticket"), Conf.NETWORK),
                        Parameter.DESTINATION);
            }
        } else if (inv.getCarrier().get() instanceof ContainerMinecart) {
            destination = ByteCartRedux.rootNode.getNode("defaultroute", "empty").getString("0.0.0");
            if ((new BC7011(null, (ContainerMinecart) inv.getCarrier().get())).setAddress(destination)) {
                return (T) new AddressBook(new Ticket(new BookFile(inv, Ticket.getTicketslot(inv), false, "ticket"), Conf.NETWORK),
                        Parameter.DESTINATION);
            }
        }
        return null;
    }

    /**
     * Creates an address with a sign as support
     *
     * @param b the sign block
     * @param line the line number
     * @return the address
     */
    public static Address getAddress(BlockSnapshot b, int line) {
        return new AddressSign(b, line);
    }

    /**
     * Creates an address with a string as internal support
     *
     * The address is resolved.
     *
     * @param s the address in the form aa.bb.cc
     * @return the address
     */
    public static Address getAddress(String s) {
        return new AddressString(s, true);
    }

    /**
     * Creates an address with a string as internal support
     *
     * The address is not resolved.
     *
     * @param s the address in the form aa.bb.cc
     * @return the address
     */
    public static Address getUnresolvedAddress(String s) {
        return new AddressString(s, false);
    }
}
