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
 * A registry that can be used as output
 */
public interface RegistryOutput extends Registry {

    /**
     * Set a specific bit in this registry to the given value.
     *
     * @param index the position, starting from most significant bit
     * @param value
     */
    void setBit(int index, boolean value);

    /**
     * Set the value of this registry to the given amount.
     *
     * @param amount
     */
    void setAmount(int amount);
}
