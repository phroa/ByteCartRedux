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
package com.github.catageek.bytecart.file;

import com.github.catageek.bytecart.ByteCartRedux;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * A class handling a file stored in a book
 */
public final class BookFile implements BCFile {

    // log2 of length of a page in bytes
    static final int PAGELOG = 8;
    static final int PAGESIZE = 1 << PAGELOG;
    static final int MAXPAGE = 20;
    static final int MAXSIZE = MAXPAGE * PAGESIZE;
    private static final String PREFIX = ByteCartRedux.rootNode.getNode("author").getString();
    private final String author;
    private final CarriedInventory<?> container;
    private final boolean binaryMode;
    private final SlotIndex slot;
    private ItemStack stack;
    private ItemStackMetaOutputStream outputStream;
    private boolean isClosed = false;


    /**
     * @param inventory the inventory
     * @param index the slot index
     * @param binary true to set binary mode
     */
    public BookFile(CarriedInventory<?> inventory, int index, boolean binary) {
        this(inventory, index, binary, ".BookFile");
    }

    /**
     * @param inventory the inventory
     * @param index the slot index
     * @param binary true to set binary mode
     * @param suffix the suffix of the author name, or null
     */
    public BookFile(CarriedInventory<?> inventory, int index, boolean binary, String suffix) {
        this.binaryMode = binary;
        this.container = inventory;
        this.slot = new SlotIndex(index);
        this.stack = inventory.query(slot).peek().orElse(null);

        if (stack == null || !stack.getItem().equals(ItemTypes.WRITTEN_BOOK)) {
            inventory.query(slot).set(stack = ItemStack.of(ItemTypes.WRITTEN_BOOK, 1));
        }

        if (!this.stack.get(Keys.BOOK_AUTHOR).isPresent() || !this.stack.get(Keys.BOOK_AUTHOR).get().toPlain().startsWith(PREFIX)) {
            if (suffix != null && suffix.length() != 0) {
                this.author = PREFIX + "." + suffix;
            } else {
                this.author = PREFIX;
            }
            this.stack.offer(Keys.BOOK_AUTHOR, Text.of(author));
        } else {
            this.author = this.stack.get(Keys.BOOK_AUTHOR).get().toPlain();
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
        return inventory.query(new SlotIndex(index)).peek().filter(stack ->
                stack.supports(Keys.BOOK_AUTHOR) && stack.get(Keys.BOOK_AUTHOR).get().toPlain().startsWith(PREFIX))
                .isPresent();
    }

    @Override
    public int getCapacity() {
        return MAXSIZE;
    }

    @Override
    public void clear() {
        if (outputStream != null) {
            this.outputStream.getBook().offer(Keys.BOOK_PAGES, new ArrayList<Text>());
        } else {
            stack.offer(Keys.BOOK_PAGES, new ArrayList<Text>());
        }
    }

    @Override
    public boolean isEmpty() {
        if (outputStream != null) {
            return !outputStream.getBook().supports(Keys.BOOK_PAGES) || outputStream.getBook().get(Keys.BOOK_PAGES).get().get(0).isEmpty();
        } else {
            return !stack.supports(Keys.BOOK_PAGES) || stack.get(Keys.BOOK_PAGES).get().get(0).isEmpty();
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }

        if (outputStream != null) {
            return outputStream;
        }

        @SuppressWarnings("resource")
        BookOutputStream bookoutputstream = binaryMode ? new Base64BookOutputStream(stack) : new BookOutputStream(stack);
        return outputStream = new ItemStackMetaOutputStream(stack, bookoutputstream);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }

        if (outputStream != null && outputStream.getBuffer().length != 0) {
            return new BookInputStream(outputStream);
        }
        return new BookInputStream(stack, binaryMode);
    }

    @Override
    public void close() throws IOException {
        if (outputStream != null) {
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
        if (outputStream != null) {
            if (isClosed) {
                throw new IOException("Book File has already been closed");
            }
            outputStream.flush();
            stack.copyFrom(outputStream.getBook());
        } else {
            this.getContainer().query(slot).set(stack);
        }
    }

    @Override
    public CarriedInventory<?> getContainer() {
        return container;
    }

    @Override
    public String getDescription() throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }
        if (outputStream != null) {
            return outputStream.getBook().get(Keys.DISPLAY_NAME).get().toPlain();
        } else {
            return stack.get(Keys.DISPLAY_NAME).get().toPlain();
        }
    }

    @Override
    public void setDescription(String s) throws IOException {
        if (isClosed) {
            throw new IOException("Book File has already been closed");
        }
        if (outputStream != null) {
            outputStream.getBook().offer(Keys.DISPLAY_NAME, Text.of(s));
            stack = outputStream.getBook();
        } else {
            stack.offer(Keys.DISPLAY_NAME, Text.of(s));
        }
    }
}
