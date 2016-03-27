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

import org.bukkit.block.Block;

/**
 * Abstract class containing common methods for all components
 */
public abstract class AbstractComponent implements Component {

    private final Block block;

    /**
     * @param block the block containing the component
     */
    protected AbstractComponent(Block block) {
        this.block = block;
    }

    /**
     * @return the block
     */
    public Block getBlock() {
        return block;
    }
}
