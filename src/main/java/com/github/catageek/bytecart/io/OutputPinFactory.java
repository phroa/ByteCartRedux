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
package com.github.catageek.bytecart.io;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.item.ItemTypes;


/**
 * Factory to get an instance of an output component
 */
public final class OutputPinFactory {

    /**
     * Get an instance of the output component
     *
     * @param block block containing the component
     * @return the instance
     */
    static public OutputPin getOutput(BlockSnapshot block) {

        if (block.getState().getType().equals(ItemTypes.LEVER)) {
            return new ComponentLever(block);
        }

        if (block.getState().getType().equals(ItemTypes.STONE_BUTTON) || block.getState().getType().equals(ItemTypes.WOODEN_BUTTON)) {
            return new ComponentButton(block);
        }

        return null;

    }

}
