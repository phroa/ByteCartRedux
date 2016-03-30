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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotPos;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

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

            Iterator<Inventory> it = inv.iterator();

            while (it.hasNext()) {
                Inventory next = it.next();
                if (isTicket(next.peek().orElse(ItemStack.builder().build()))) {
                    Vector2i value = next.getProperty(SlotPos.class, 0).get().getValue();
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
    private final static boolean isTicket(ItemStack stack) {
        if (stack != null && stack.getType().equals(Material.WRITTEN_BOOK) && stack.hasItemMeta()) {
            String bookauthor = ((BookMeta) stack.getItemMeta()).getAuthor();
            if (bookauthor.equals(ByteCartRedux.rootNode.getNode("author").getString())) {
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
    static int getEmptyOrBookAndQuillSlot(Inventory inv) {

        ItemStack stack;

        if (ByteCartRedux.rootNode.getNode("mustProvideBooks").getBoolean()
                && inv.contains(Material.BOOK_AND_QUILL)) {

            // priority given to book in hand
            if (inv.getHolder() instanceof Player) {
                Player player = (Player) inv.getHolder();
                if (isEmptyBook(stack = player.getItemInHand())) {
                    return player.getInventory().getHeldItemSlot();
                }
            }


            ListIterator<? extends ItemStack> it = inv.iterator();

            while (it.hasNext()) {
                stack = it.next();

                if (isEmptyBook(stack)) {
                    int slot = it.previousIndex();
                    // found a book
                    return slot;
                }
            }
        }

        // no book found or user must provide one, return empty slot
        int slot = inv.firstEmpty();

        if (slot != -1) {
            return slot;
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
        if ((slot = getEmptyOrBookAndQuillSlot(player.getInventory())) == -1) {
            String msg = "Error: No space in inventory.";
            player.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + msg);
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
    static int searchSlot(Inventory inv) {
        int slot;
        // get a slot containing an emtpy book (or nothing)
        slot = Ticket.getEmptyOrBookAndQuillSlot(inv);

        if (slot != -1) {
            if (inv.getItem(slot) == null
                    && ByteCartRedux.rootNode.getNode("mustProvideBooks").getBoolean()
                    && ByteCartRedux.rootNode.getNode("usebooks").getBoolean()) {
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
    static void createTicket(Inventory inv, int slot) {

        if (slot == -1) {
            return;
        }

        ItemStack stack = getBookStack(ByteCartRedux.rootNode.getNode("author").getString(),
                ByteCartRedux.rootNode.getNode("title").getString());

        // swap with an existing book if needed
        int existingticket = Ticket.getTicketslot(inv);
        if (existingticket != -1 && existingticket != slot) {
            inv.setItem(slot, inv.getItem(existingticket));
            slot = existingticket;
        }

        inv.setItem(slot, stack);
    }

    /**
     * Return an ItemStack containing a ticket
     *
     * @param author the name of the author
     * @param title the name of the ticket
     * @return the ItemStack
     */
    private static ItemStack getBookStack(String author, String title) {
        ItemStack stack;
        /* Here we create a ticket in slot, replacing empty book if needed */
        BookMeta book;

        book = (BookMeta) Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        book.setAuthor(author);
        book.setTitle(title);
        stack = new ItemStack(Material.WRITTEN_BOOK);
        stack.setItemMeta(book);
        return stack;
    }


    /**
     * Tell if a book_and_quill is empty
     *
     * @param stack the ItemStack to check
     * @return true if it is an empty book_and_quill
     */
    private static boolean isEmptyBook(ItemStack stack) {
        BookMeta book;

        if (stack != null && stack.getType().equals(Material.BOOK_AND_QUILL)) {

            if (stack.hasItemMeta()) {

                if ((book = (BookMeta) stack.getItemMeta()).hasPages()
                        && (book.getPage(1).isEmpty())) {
                    return true;
                }
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the holder of the ticket
     *
     * @return the holder
     */
    InventoryHolder getTicketHolder() {
        return properties.getFile().getContainer().getHolder();
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
        try {
            properties.setProperty(parameter.getName(), value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
     *
     * @param name
     * @param s
     */
    void appendTitle(String name, String s) {
        StringBuilder build = new StringBuilder(ByteCartRedux.rootNode.getNode("title").getString());
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
        try {
            properties.setProperty(parameter.getName(), defaultvalue);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Remove a parameter
     *
     * <p>{@link Ticket#close()} must be called to actually perform the operation</p>
     *
     * @param parameter parameter to remove
     */
    void remove(Parameter parameter) {
        try {
            properties.clearProperty(parameter.getName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

