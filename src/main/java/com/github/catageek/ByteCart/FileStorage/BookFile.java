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
package com.github.catageek.ByteCart.FileStorage;

import com.github.catageek.ByteCart.ByteCartRedux;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A class handling a file stored in a book
 */
public final class BookFile implements BCFile {

    // log2 of length of a page in bytes
    static final int PAGELOG = 8;
    static final int PAGESIZE = 1 << PAGELOG;
    static final int MAXPAGE = 20;
    static final int MAXSIZE = MAXPAGE * PAGESIZE;
    private static final String prefix = ByteCartRedux.myPlugin.getConfig().getString("author");
    private final String author;
    private final Inventory container;
    private final boolean binarymode;
    private final int slot;
    private BookMeta book;
    private ItemStack stack;
    private ItemStackMetaOutputStream outputstream;
    private boolean isClosed = false;


    /**
     * @param inventory the inventory
     * @param index the slot index
     * @param binary true to set binary mode
     */
    public BookFile(Inventory inventory, int index, boolean binary) {
        this(inventory, index, binary, ".BookFile");
    }

    /**
     * @param inventory the inventory
     * @param index the slot index
     * @param binary true to set binary mode
     * @param name the suffix of the author name, or null
     */
    public BookFile(Inventory inventory, int index, boolean binary, String name) {
        this.binarymode = binary;
        this.container = inventory;
        this.slot = index;
        this.stack = inventory.getItem(index);
        if (stack == null || !stack.getType().equals(Material.WRITTEN_BOOK)) {
            inventory.setItem(index, (stack = new ItemStack(Material.WRITTEN_BOOK)));
        }
        this.book = (BookMeta) (stack.hasItemMeta() ? stack.getItemMeta() : Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK));

        // fix corrupted books in MC 1.8
        try {
            List<String> test = this.book.getPages();
        } catch (NullPointerException e) {
            inventory.setItem(index, (stack = new ItemStack(Material.WRITTEN_BOOK)));
            this.book =
                    (BookMeta) (stack.hasItemMeta() ? stack.getItemMeta() : Bukkit.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK));
        }

        if (!this.book.hasAuthor() || !this.book.getAuthor().startsWith(prefix)) {
            if (name != null && name.length() != 0) {
                this.author = prefix + "." + name;
            } else {
                this.author = prefix;
            }
            this.book.setAuthor(author);
        } else {
            this.author = this.book.getAuthor();
        }
    }

    /**
     * Tell if a slot of an inventory contains a file in a book
     *
     * @param inventory the inventory
     * @param index the slot number
     * @return true if the slot contains a file and the author field begins with author configuration parameter
     */
    public static boolean isBookFile(Inventory inventory, int index) {
        ItemStack stack = inventory.getItem(index);
        if (stack != null && stack.getType().equals(Material.WRITTEN_BOOK) && stack.hasItemMeta()) {
            return ((BookMeta) stack.getItemMeta()).getAuthor().startsWith(prefix);
        }
        return false;
    }

    @Override
    public int getCapacity() {
        return MAXSIZE;
    }

    @Override
    public void clear() {
        if (outputstream != null) {
            this.outputstream.getBook().setPages(new ArrayList<String>());
        } else {
            book.setPages(new ArrayList<String>());
        }
    }

    @Override
    public boolean isEmpty() {
        if (outputstream != null) {
            return !outputstream.getBook().hasPages() || outputstream.getBook().getPage(1).length() == 0;
        } else {
            return !book.hasPages() || book.getPage(1).length() == 0;
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }

        if (outputstream != null) {
            return outputstream;
        }

        @SuppressWarnings("resource")
        BookOutputStream bookoutputstream = binarymode ? new Base64BookOutputStream(book) : new BookOutputStream(book);
        return outputstream = new ItemStackMetaOutputStream(stack, bookoutputstream);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }

        if (outputstream != null && outputstream.getBuffer().length != 0) {
            return new BookInputStream(outputstream);
        }
        return new BookInputStream(book, binarymode);
    }

    @Override
    public void close() throws IOException {
        if (outputstream != null) {
            if (isClosed) {
                throw new IOException("Book File has already been closed");
            }
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : BookFile : closing");
            }
            isClosed = true;
        }

    }

    @Override
    public void flush() throws IOException {
        if (outputstream != null) {
            if (isClosed) {
                throw new IOException("Book File has already been closed");
            }
            outputstream.flush();
            book = outputstream.getBook();
        } else {
            stack.setItemMeta(book);
            this.getContainer().setItem(slot, stack);
        }
    }

    @Override
    public Inventory getContainer() {
        return container;
    }

    @Override
    public String getDescription() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }
        if (outputstream != null) {
            return outputstream.getBook().getTitle();
        } else {
            return book.getTitle();
        }
    }

    @Override
    public void setDescription(String s) throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }
        if (outputstream != null) {
            outputstream.getBook().setTitle(s);
            book = outputstream.getBook();
        } else {
            book.setTitle(s);
        }
    }
}
