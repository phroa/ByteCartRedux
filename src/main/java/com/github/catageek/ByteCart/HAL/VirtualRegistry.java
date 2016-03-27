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
package com.github.catageek.ByteCart.HAL;

import java.io.Serializable;

/**
 * A registry with an internal implementation
 */
public class VirtualRegistry implements RegistryBoth, Serializable {

    // bit index 0 est le bit de poids fort

    private static final long serialVersionUID = -7296392835005177288L;
    private final int Length;
    private int Virtual = 0;

    /**
     * @param length the length of the registry to create
     */
    public VirtualRegistry(int length) {
        this.Length = length;
    }

    @Override
    public void setBit(int index, boolean value) {

        if (value) {
            this.Virtual |= 1 << (length() - index - 1);
        } else {
            this.Virtual &= ~(1 << (length() - index - 1));
        }
    }

    @Override
    public boolean getBit(int index) {
        if (((this.Virtual >> (length() - index - 1)) & 1) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public int length() {
        return this.Length;
    }

    @Override
    public int getAmount() {
        return Virtual;
    }

    @Override
    public void setAmount(int amount) {
        this.Virtual = amount % (1 << this.length());
    }

}
