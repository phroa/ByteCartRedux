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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;

/**
 * An outputstream for a book in an ItemStack. Write operations in the book update the ItemStack object 
 */
final class ItemStackMetaOutputStream extends ItemStackOutputStream {

    private final BookOutputStream OutputStream;
    private boolean isClosed = false;


    /**
     * @param stack the stack containing the book
     * @param outputstream an output stream for the book
     */
    ItemStackMetaOutputStream(ItemStack stack, BookOutputStream outputstream) {
        super(stack);
        OutputStream = outputstream;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] cbuf, int off, int len) throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        OutputStream.write(cbuf, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        OutputStream.flush();
        getItemStack().setItemMeta(OutputStream.getBook());
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Flushing meta to itemstack");
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Closing itemstack");
        }
        OutputStream.close();
        isClosed = true;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        OutputStream.write(b);
    }

    /**
     * Get the current buffer
     *
     * @return the buffer
     */
    final byte[] getBuffer() {
        return OutputStream.getBuffer();
    }

    /**
     * Get the book
     *
     * @return the book
     */
    final BookMeta getBook() {
        return OutputStream.getBook();
    }
}
