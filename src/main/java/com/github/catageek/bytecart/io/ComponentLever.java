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
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BooleanTraits;
import org.spongepowered.api.data.key.Keys;

/**
 * A lever
 */
public class ComponentLever extends AbstractComponent implements OutputPin, InputPin, RegistryInput {

    /**
     * @param block the block containing the component
     */
    public ComponentLever(BlockSnapshot block) {
        super(block);
    }

    @Override
    public void write(boolean bit) {
        BlockState block = this.getBlock().getState();
        boolean powered = block.getTraitValue(BooleanTraits.LEVER_POWERED).orElse(false);
        if (powered != bit) {
            block.withTrait(BooleanTraits.LEVER_POWERED, bit);
            MathUtil.forceUpdate(this.getBlock().getLocation().get().getRelative(block.get(Keys.DIRECTION).get().getOpposite()).createSnapshot());
        }
    }

    @Override
    public boolean read() {
        return this.getBlock().getState().getTraitValue(BooleanTraits.LEVER_POWERED).orElse(false);
    }

    @Override
    public boolean getBit(int index) {
        return read();
    }

    @Override
    public int getValue() {
        return (read() ? 15 : 0);
    }

    @Override
    public int length() {
        return 4;
    }


}
