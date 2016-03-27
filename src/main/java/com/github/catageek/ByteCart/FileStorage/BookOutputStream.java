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
import org.bukkit.inventory.meta.BookMeta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * An output stream to write in book
 */
class BookOutputStream extends ByteArrayOutputStream {

    private final BookMeta book;

    private boolean isClosed = false;

    BookOutputStream(BookMeta book) {
        super(book.getPageCount() * BookFile.PAGESIZE);
        this.book = book;
    }

    /* (non-Javadoc)
     * @see java.io.ByteArrayOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] bytes, int off, int len) {
        //		this.reset();
        //		if(ByteCartRedux.debug)
        //			ByteCartRedux.log.info("ByteCartRedux : empty data cache buffer");

        super.write(bytes, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] bytes) throws IOException {
        if (isClosed) {
            throw new IOException("Book has been already closed");
        }
        this.write(bytes, 0, bytes.length);
    }

    /**
     * Get the content as a byte array
     *
     * @return the buffer
     */
    protected byte[] getBuffer() {
        return this.toByteArray();
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {

        if (isClosed) {
            throw new IOException("Book has been already closed");
        }

        if (this.size() == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder(getEncodedString());

        int len = sb.length();
        int i, j = 1;


        // number of pages to write less 1
        int count = len / BookFile.PAGESIZE;

        String[] strings = new String[count + 1];

        // loop for full pages
        for (i = 0; i < count; i++) {
            strings[i] = sb.substring(i << BookFile.PAGELOG, j << BookFile.PAGELOG);
            j++;
        }

        // last page
        strings[count] = sb.substring(i << BookFile.PAGELOG);

        this.book.setPages(strings);

        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : Flushing " + len + " bytes of data to meta");
        }
    }

    /**
     * Get the content as a string
     *
     * @return the content of the book
     */
    protected String getEncodedString() {
        return this.toString();
    }

    /* (non-Javadoc)
     * @see java.io.ByteArrayOutputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (isClosed) {
            throw new IOException("Book has been already closed");
        }
        isClosed = true;
    }

    /**
     * Get the book
     *
     * @return the book
     */
    final BookMeta getBook() {
        return book;
    }
}

