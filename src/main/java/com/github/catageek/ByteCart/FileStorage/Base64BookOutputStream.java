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
import org.bukkit.inventory.meta.BookMeta;

/**
 * Base64 encoder/decoder for BookOutPutStream
 */
final class Base64BookOutputStream extends BookOutputStream {

    public Base64BookOutputStream(BookMeta book) {
        super(book);
    }

    @Override
    protected String getEncodedString() {
        return Base64.encodeToString(buf, false);
    }

    @Override
    protected byte[] getBuffer() {
        return Base64.encodeToByte(buf, false);
    }
}
