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

import com.github.catageek.bytecart.hardware.RegistryInput;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;


/**
 * A Redstone wire
 */
public class ComponentWire extends AbstractComponent implements InputPin, RegistryInput {

    /**
     * @param block the block containing the wire
     */
    public ComponentWire(BlockSnapshot block) {
        super(block);
    }

    @Override
    public boolean read() {
        return this.getBlock().getState().get(Keys.POWER).filter(i -> i > 0).isPresent();
    }

    @Override
    public boolean getBit(int index) {
        return (this.getBlock().getState().get(Keys.POWER).get() & 1 << (length() - index)) != 0;
    }

    @Override
    public int getValue() {
        return this.getBlock().getState().get(Keys.POWER).get();
    }

    @Override
    public int length() {
        return 4;
    }


}
