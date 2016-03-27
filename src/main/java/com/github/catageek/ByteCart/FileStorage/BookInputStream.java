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

import com.github.catageek.ByteCart.Util.Base64;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;

import java.io.ByteArrayInputStream;

/**
 * An input stream to read from book
 */
class BookInputStream extends ByteArrayInputStream {

    /**
     * @param book the book
     * @param binary set binary mode
     */
    BookInputStream(BookMeta book, boolean binary) {
        super(readPages(book, binary));
    }

    /**
     * @param outputstream the output stream from where we read data
     */
    BookInputStream(ItemStackMetaOutputStream outputstream) {
        super(outputstream.getBuffer());
    }

    /**
     * Copy all pages of a book in a array of bytes
     *
     * @param book the book
     * @param binary binary mode
     * @return the array of bytes
     */
    private static byte[] readPages(BookMeta book, boolean binary) {
        int len = book.getPageCount() << BookFile.PAGELOG;
        StringBuilder sb = new StringBuilder(len);

        for (int i = 1; i <= book.getPageCount(); ++i) {
            sb.append(ChatColor.stripColor(book.getPage(i)));
        }

        sb.trimToSize();
        if (binary) {
            return Base64.decodeFast(sb.toString());
        }
        return sb.toString().getBytes();
    }
}