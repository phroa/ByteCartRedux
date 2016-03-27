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
package com.github.catageek.bytecart.hardware;

// This class represents 2 registries merged

/**
 * A registry grouping 2 registries
 *
 * @param <T> A type extending Registry
 */
public class SuperRegistry<T extends Registry> implements RegistryBoth {

    // first est le registre de poids fort

    private final T first;
    private final T second;

    /**
     * @param reg1 The left part, i.e MSB side
     * @param reg2 The right part, i.e LSB side
     */
    public SuperRegistry(T reg1, T reg2) {
        this.first = reg1;
        this.second = reg2;
    }

    @Override
    public void setBit(int index, boolean value) {
        if (index < this.first.length()) {
            ((RegistryOutput) this.first).setBit(index, value);
        } else {
            ((RegistryOutput) this.second).setBit(index - this.first.length(), value);
        }

    }

    @Override
    public boolean getBit(int index) {
        if (index < this.first.length()) {
            return ((RegistryInput) this.first).getBit(index);
        }
        return ((RegistryInput) this.second).getBit(index - this.first.length());
    }

    @Override
    public int length() {
        return this.first.length() + this.second.length();
    }

    @Override
    public int getValue() {
        return (this.first.getValue() << this.second.length()) + this.second.getValue();
    }

    @Override
    public void setAmount(int amount) {
        ((RegistryOutput) this.first).setAmount(amount >> (this.second.length()) % (1 << this.first.length()));
        ((RegistryOutput) this.second).setAmount(amount & ((1 << this.second.length()) - 1));
    }

}
