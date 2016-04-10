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

import com.flowpowered.math.vector.Vector2i;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.address.AddressBook.Parameter;
import com.github.catageek.bytecart.file.BookFile;
import com.github.catageek.bytecart.file.BookProperties;
import com.github.catageek.bytecart.file.BookProperties.Conf;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;

import java.io.IOException;

/**
 * Implement a ticket
 */
final class Ticket {

    /**
     * Internal storage of the ticket
     */
    private final BookProperties properties;

    /**
     * Create a ticket using a book at a specific page
     *
     * @param bookfile the book FS to use
     * @param network the name of the page
     */
    Ticket(BookFile bookfile, Conf network) {
        properties = new BookProperties(bookfile, network);
    }

    /**
     * Get a slot containing a ticket
     *
     *
     * @param inv The inventory to search in
     * @return a slot number, or -1
     */
    static int getTicketslot(Inventory inv) {
        if (inv.contains(ItemTypes.WRITTEN_BOOK)) {

            // priority given to book in hand
            if (inv instanceof HumanInventory) {
                Player player = ((Player) ((HumanInventory) inv).getCarrier().get());
                if (isTicket(player.getEquipped(EquipmentTypes.EQUIPPED).get())) {
                    return ((HumanInventory) player.getInventory()).getHotbar().getSelectedSlotIndex();
                }
            }

            for (Inventory slot : inv) {
                if (isTicket(slot.peek().orElse(ItemStack.builder().build()))) {
                    Vector2i value = slot.getProperty(SlotPos.class, 0).get().getValue();
                    return value.getX() + value.getY() * 9;
                }
            }
        }
        return -1;
    }

    /**
     * Tell if a stack is a ticket
     *
     * @param stack the stack to check
     * @return true if it is a ticket
     */
    private static boolean isTicket(ItemStack stack) {
        if (stack != null && stack.getItem().equals(ItemTypes.WRITTEN_BOOK)) {
            String bookAuthor = stack.get(Keys.BOOK_AUTHOR).get().toPlain();
            if (bookAuthor.equals(ByteCartRedux.rootNode.getNode("book", "author").getString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a slot containing an empty book or nothing
     *
     *
     * @param inv The inventory to search in
     * @return a slot number, or -1
     */
    private static <T extends CarriedInventory<?> & OrderedInventory> int getEmptyOrBookAndQuillSlot(T inv) {

        if (ByteCartRedux.rootNode.getNode("book", "mustprovide").getBoolean()
                && inv.contains(ItemTypes.WRITABLE_BOOK)) {

            // priority given to book in hand
            if (inv.getCarrier().get() instanceof Player) {
                Player player = (Player) inv.getCarrier().get();
                if (isEmptyBook(player.getEquipped(EquipmentTypes.EQUIPPED).orElse(ItemStack.of(ItemTypes.STONE, 1)))) {
                    return ((HumanInventory) player.getInventory()).getHotbar().getSelectedSlotIndex();
                }
            }


            for (int slot = 0; slot < inv.size(); slot++) {
                if (isEmptyBook(inv.getSlot(new SlotIndex(slot)).get().peek().orElse(ItemStack.of(ItemTypes.STONE, 1)))) {
                    return slot;
                }
            }
        }

        // no book found or user must provide one, return empty slot
        for (int slot = 0; slot < inv.size(); slot++) {
            if (inv.getSlot(new SlotIndex(slot)).get().isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Get a slot containing an empty book or nothing
     *
     *
     * @param player The player having the inventory to search in
     * @return a slot number, or -1
     */
    static int getEmptyOrBookAndQuillSlot(Player player) {
        int slot;
        if ((slot = getEmptyOrBookAndQuillSlot(((HumanInventory & OrderedInventory) player.getInventory()))) == -1) {
            Messaging.sendError(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "inventoryspace").getString()));
        }
        return slot;
    }

    /**
     * Get a slot containing an empty book or nothing
     *
     *
     * @param inv The inventory to search in
     * @return a slot number, or -1
     */
    static <T extends OrderedInventory & CarriedInventory<?>> int searchSlot(T inv) {
        int slot;
        // get a slot containing an emtpy book (or nothing)
        slot = Ticket.getEmptyOrBookAndQuillSlot(inv);

        if (slot != -1) {
            if (inv.getSlot(new SlotIndex(slot)).filter(Inventory::isEmpty).isPresent()
                    && ByteCartRedux.rootNode.getNode("book", "mustprovide").getBoolean()
                    && ByteCartRedux.rootNode.getNode("book", "use").getBoolean()) {
                return -1;
            }
            return slot;
        }
        return -1;
    }

    /**
     * Initialize a ticket in a inventory
     *
     * @param inv the inventory where to put the ticket
     * @param slot the slot number where to put the ticket
     */
    static void createTicket(OrderedInventory inv, int slot) {

        if (slot == -1) {
            return;
        }

        ItemStack stack = getBookStack(ByteCartRedux.rootNode.getNode("book", "author").getString(),
                ByteCartRedux.rootNode.getNode("book", "title").getString());

        // swap with an existing book if needed
        int existingticket = Ticket.getTicketslot(inv);
        if (existingticket != -1 && existingticket != slot) {
            inv.set(new SlotIndex(slot), inv.getSlot(new SlotIndex(existingticket)).get().peek().get());
            slot = existingticket;
        }

        inv.set(new SlotIndex(slot), stack);
    }

    /**
     * Return an ItemStack containing a ticket
     *
     * @param author the name of the author
     * @param title the name of the ticket
     * @return the ItemStack
     */
    private static ItemStack getBookStack(String author, String title) {
        ItemStack stack = ItemStack.of(ItemTypes.WRITTEN_BOOK, 1);
        /* Here we create a ticket in slot, replacing empty book if needed */
        stack.offer(Keys.BOOK_AUTHOR, Text.of(author));
        stack.offer(Keys.DISPLAY_NAME, Text.of(title));
        return stack;
    }


    /**
     * Tell if a book_and_quill is empty
     *
     * @param stack the ItemStack to check
     * @return true if it is an empty book_and_quill
     */
    private static boolean isEmptyBook(ItemStack stack) {
        return stack != null && stack.getItem().equals(ItemTypes.WRITABLE_BOOK) && !stack.get(Keys.BOOK_PAGES).filter(texts -> !texts.isEmpty())
                .isPresent();

    }

    /**
     * Get the holder of the ticket
     *
     * @return the holder
     */
    Carrier getTicketHolder() {
        return properties.getFile().getContainer().getCarrier().get();
    }

    /**
     * Set a parameter value in the ticket
     *
     * <p>{@link Ticket#close()} must be called to actually perform the operation</p>
     *
     * @param parameter parameter to set
     * @param value value to set
     * @return true
     */
    final boolean setEntry(Parameter parameter, String value) {
        properties.setProperty(parameter.getName(), value);
        return true;
    }

    /**
     * Return the value of a parameter or a default value
     *
     * @param parameter the parameter to return
     * @param defaultvalue the default value
     * @return a string containing the parameter value, or the default value
     */
    final String getString(Parameter parameter, String defaultvalue) {
        return properties.getString(parameter.getName(), defaultvalue);
    }

    /**
     * Return the value of a parameter
     *
     * @param parameter the parameter to return
     * @return a string containing the parameter value
     */
    final String getString(Parameter parameter) {
        return properties.getString(parameter.getName());
    }

    /**
     * Append a name and a string to the title
     *
     * <p>{@link Ticket#close()} must be called to actually perform the operation</p>
     *
     * <p>The appended value is " name (string)"</p>
     */
    void appendTitle(String name, String s) {
        StringBuilder build = new StringBuilder(ByteCartRedux.rootNode.getNode("book", "title").getString());
        if (name != null) {
            build.append(" ").append(name);
        }
        build.append(" ").append(s);
        try {
            properties.getFile().setDescription(build.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get a parameter value as an integer or a default value
     *
     * @param parameter the parameter to get
     * @param defaultvalue the default value
     * @return the parameter value
     */
    final int getInt(Parameter parameter, int defaultvalue) {
        return properties.getInt(parameter.getName(), defaultvalue);
    }

    /**
     * Reset the parameter to default value
     *
     * <p>{@link Ticket#close()} must be called to actually perform the operation</p>
     *
     * @param parameter the parameter to set
     * @param defaultvalue the value to set
     */
    void resetValue(Parameter parameter, String defaultvalue) {
        properties.setProperty(parameter.getName(), defaultvalue);
    }

    /**
     * Remove a parameter
     *
     * <p>{@link Ticket#close()} must be called to actually perform the operation</p>
     *
     * @param parameter parameter to remove
     */
    void remove(Parameter parameter) {
        properties.clearProperty(parameter.getName());
    }

    /**
     * Write the parameters and close the ticket
     *
     */
    void close() {
        try {
            properties.flush();
            properties.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

