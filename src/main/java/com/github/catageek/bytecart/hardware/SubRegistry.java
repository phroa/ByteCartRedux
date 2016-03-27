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

    private final Registry Registry;
    private final int Length;
    private final int First;

    /**
     * @param reg the original registry
     * @param length the length of the restricted view
     * @param first the index of the first bit of the restricted view
     */
    public SubRegistry(T reg, int length, int first) {
        this.Registry = reg;
        this.Length = length;
        this.First = first;

    }

    @Override
    public int length() {
        return this.Length;
    }

    @Override
    public int getAmount() {
        return (this.Registry.getAmount() >> (this.Registry.length() - (this.First + this.length())) & (1 << this.length()) - 1);
    }

    @Override
    public void setAmount(int amount) {
        ((RegistryOutput) this.Registry).setAmount(
                this.Registry.getAmount() - this.getAmount() + ((amount % (1 << this.length())) << (this.Registry.length() - (this.First + this
                        .length()))));
    }

    @Override
    public void setBit(int index, boolean value) {
        ((RegistryOutput) this.Registry).setBit(index + this.First, value);
    }

    @Override
    public boolean getBit(int index) {
        return ((RegistryInput) this.Registry).getBit(index + this.First);
    }

}