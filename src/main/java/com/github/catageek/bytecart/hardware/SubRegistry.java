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

/**
 * A restricted view of a registry
 *
 * @param <T> A type implementing Registry
 */
public class SubRegistry<T extends Registry> implements RegistryBoth {

    private final T registry;
    private final int length;
    private final int first;

    /**
     * @param reg the original registry
     * @param length the length of the restricted view
     * @param first the index of the first bit of the restricted view
     */
    public SubRegistry(T reg, int length, int first) {
        this.registry = reg;
        this.length = length;
        this.first = first;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public int getValue() {
        return (this.registry.getValue() >> (this.registry.length() - (this.first + this.length())) & (1 << this.length()) - 1);
    }

    @Override
    public void setAmount(int amount) {
        ((RegistryOutput) this.registry).setAmount(
                this.registry.getValue() - this.getValue() + ((amount % (1 << this.length())) << (this.registry.length() - (this.first + this
                        .length()))));
    }

    @Override
    public void setBit(int index, boolean value) {
        ((RegistryOutput) this.registry).setBit(index + this.first, value);
    }

    @Override
    public boolean getBit(int index) {
        return ((RegistryInput) this.registry).getBit(index + this.first);
    }

}
