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

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.FileStorage.BookOutputStream#getEncodedString()
     */
    @Override
    protected String getEncodedString() {
        return Base64.encodeToString(buf, false);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.FileStorage.BookOutputStream#getBuffer()
     */
    @Override
    protected byte[] getBuffer() {
        return Base64.encodeToByte(buf, false);
    }
}
