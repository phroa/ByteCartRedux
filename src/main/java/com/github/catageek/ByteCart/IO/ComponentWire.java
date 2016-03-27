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

import com.github.catageek.ByteCart.HAL.RegistryInput;
import org.bukkit.block.Block;
import org.bukkit.material.RedstoneWire;


/**
 * A Redstone wire
 */
public class ComponentWire extends AbstractComponent implements InputPin, RegistryInput {

    /**
     * @param block the block containing the wire
     */
    public ComponentWire(Block block) {
        super(block);
    }

    @Override
    public boolean read() {
        return ((RedstoneWire) this.getBlock().getState().getData()).isPowered();
    }

    @Override
    public boolean getBit(int index) {
        RedstoneWire wire = ((RedstoneWire) this.getBlock().getState().getData());
        return (wire.getData() & 1 << (length() - index)) != 0;
    }

    @Override
    public int getAmount() {
        return ((RedstoneWire) this.getBlock().getState().getData()).getData();
    }

    @Override
    public int length() {
        return 4;
    }


}
