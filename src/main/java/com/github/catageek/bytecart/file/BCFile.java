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

import org.bukkit.inventory.Inventory;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a file with various operations
 */
public interface BCFile extends Closeable, Flushable {

    /**
     * Get the capacity, in bytes, of the file
     *
     * @return the capacity
     */
    int getCapacity();

    /**
     * clear the file content
     *
     */
    void clear();

    /**
     * Tell if a file is empty
     *
     * @return true if empty
     */
    boolean isEmpty();

    /**
     * Get an output stream for this file
     *
     * @return the stream
     * @throws IOException
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Get an input stream
     *
     * @return the stream
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get the inventory containing this file
     *
     * @return the inventory
     */
    Inventory getContainer();

    /**
     * Get the title for this file
     *
     * @return the title
     * @throws IOException
     */
    String getDescription() throws IOException;

    /**
     * Set a title to this file
     *
     * @param s the title to set
     * @throws IOException
     */
    void setDescription(String s) throws IOException;
}
