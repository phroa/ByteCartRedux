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

import com.github.catageek.bytecart.ByteCartRedux;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * A sign
 */
public final class ComponentSign extends AbstractComponent {

    /**
     * @param block the block containing the component
     */
    public ComponentSign(BlockSnapshot block) {
        super(block);
    }

    /**
     * Set a line of the sign
     *
     * @param line index of the line
     * @param s the text to write
     */
    public void setLine(int line, String s) {
        BlockState blockstate = this.getBlock().getState();
        if (blockstate.supports(Keys.SIGN_LINES)) {
            List<Text> list = blockstate.get(Keys.SIGN_LINES).get();
            list.set(line, Text.of(s));
            blockstate.with(Keys.SIGN_LINES, list);
        }
    }

    /**
     * Get a line of a sign
     *
     * @param line index of the line
     * @return the text
     */
    public String getLine(int line) {
        BlockState blockstate = this.getBlock().getState();
        if (blockstate.supports(Keys.SIGN_LINES)) {
            return blockstate.get(Keys.SIGN_LINES).get().get(line).toPlain();
        } else {
            ByteCartRedux.log.info("ByteCartRedux: AddressSign cannot be built");
            throw new IllegalArgumentException();
        }

    }
}
