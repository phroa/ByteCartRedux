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
package com.github.catageek.ByteCart.IO;

import org.bukkit.Material;
import org.bukkit.block.Block;


/**
 * A factory for input pins
 */
final public class InputFactory {

    /**
     * Get an instance of the input component
     *
     * @param block block containing the component
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    static public <T> T getInput(Block block) {

        if (block.getType().equals(Material.REDSTONE_WIRE)) {
            return (T) new ComponentWire(block);
        }
        if (block.getType().equals(Material.LEVER)) {
            return (T) new ComponentLever(block);
        }
        return null;

    }

}
