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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Factory to create or get a ticket
 */
public final class TicketFactory {

    /**
     * Put a ticket in a player's inventory if necessary
     *
     * @param player the player
     * @param forcereuse must be true to force the reuse of existing ticket
     */
    public static final void getOrCreateTicket(Player player, boolean forcereuse) {
        int slot;
        Inventory inv = player.getInventory();

        if (forcereuse || ByteCartRedux.rootNode.getNode("reusetickets").getBoolean(true)) {
            // if storage cart or we must reuse a existing ticket
            // check if a ticket exists and return
            // otherwise continue
            slot = Ticket.getTicketslot(inv);
            if (slot != -1) {
                return;
            }
        }

        // get a slot containing an emtpy book (or nothing)
        if ((slot = Ticket.getEmptyOrBookAndQuillSlot(player)) == -1) {
            return;
        }

        if (inv.query(new SlotIndex(slot)).isEmpty() && ByteCartRedux.rootNode.getNode("mustProvideBooks").getBoolean()) {
            String msg = "No empty book in your inventory, you must provide one.";
            player.sendMessage(
                    Text.builder().color(TextColors.DARK_GREEN).append(Text.of("[Bytecart] ")).color(TextColors.RED).append(Text.of(msg)).build());
            return;
        }

        Ticket.createTicket(inv, slot);
    }

    /**
     * Put a ticket in an inventory, if necessary. The inventory is not updated.
     *
     * @param inv the inventory where to put the ticket
     */
    public static final void getOrCreateTicket(Inventory inv) {
        int slot;

        // if storage cart or we must reuse a existing ticket
        // check if a ticket exists and return
        // otherwise continue
        slot = Ticket.getTicketslot(inv);
        if (slot != -1) {
            return;
        }

        Ticket.createTicket(inv, Ticket.searchSlot(inv));

    }

    /**
     * Remove a ticket from an inventory
     *
     * @param inv
     */
    public static final void removeTickets(Inventory inv) {
        int slot;
        while ((slot = Ticket.getTicketslot(inv)) != -1) {
            inv.query(new SlotIndex(slot)).clear();
        }
    }
}
