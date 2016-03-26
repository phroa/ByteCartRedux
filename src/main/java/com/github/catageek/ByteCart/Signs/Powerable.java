package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.HAL.IC;

import java.io.IOException;

/**
 * An IC that can be powered should implement this
 */
public interface Powerable extends IC {

    /**
     * Method called when the IC is powered
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void power() throws ClassNotFoundException, IOException;

}
