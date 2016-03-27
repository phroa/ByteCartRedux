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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;

/**
 * A lever
 */
public class ComponentLever extends AbstractComponent implements OutputPin, InputPin, RegistryInput {

    /**
     * @param block the block containing the component
     */
    public ComponentLever(Block block) {
        super(block);
    }

    @Override
    public void write(boolean bit) {
        BlockState block = this.getBlock().getState();
        Lever lever = (Lever) block.getData();
        if (lever.isPowered() ^ bit) {
            lever.setPowered(bit);
            block.setData(lever);
            block.update(false, true);
            MathUtil.forceUpdate(this.getBlock().getRelative(lever.getAttachedFace()));
        }
    }

    @Override
    public boolean read() {
        MaterialData md = this.getBlock().getState().getData();
        if (md instanceof Lever) {
            return ((Lever) md).isPowered();
        }
        return false;
    }

    @Override
    public boolean getBit(int index) {
        return read();
    }

    @Override
    public int getAmount() {
        return (read() ? 15 : 0);
    }

    @Override
    public int length() {
        return 4;
    }


}
