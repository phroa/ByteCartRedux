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

import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.Button;

import java.util.Map;

/**
 * this call represents a thread that powers off a button
 */
public class SetButtonOff implements Runnable {

    final private Component component;
    final private Map<Location, Integer> ActivatedButtonMap;

    /**
     * @param component the component to power off
     * @param ActivatedButtonMap a map containing the task id of current task
     */
    public SetButtonOff(Component component, Map<Location, Integer> ActivatedButtonMap) {
        this.component = component;
        this.ActivatedButtonMap = ActivatedButtonMap;
    }

    @Override
    public void run() {

        BlockState block = component.getBlock().getState();

        if (block.getData() instanceof Button) {
            Button button = (Button) block.getData();

            button.setPowered(false);
            block.setData(button);

            block.update(false, true);
            MathUtil.forceUpdate(component.getBlock().getRelative(button.getAttachedFace()));
        }

        ActivatedButtonMap.remove(block.getLocation());
    }
}
