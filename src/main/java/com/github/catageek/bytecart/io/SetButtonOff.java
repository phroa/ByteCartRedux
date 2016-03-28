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

import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.UUID;

/**
 * this call represents a thread that powers off a button
 */
public class SetButtonOff implements Runnable {

    final private Component component;
    final private Map<Location<World>, UUID> activatedButtonMap;

    /**
     * @param component the component to power off
     * @param activatedButtonMap a map containing the task id of current task
     */
    public SetButtonOff(Component component, Map<Location<World>, UUID> activatedButtonMap) {
        this.component = component;
        this.activatedButtonMap = activatedButtonMap;
    }

    @Override
    public void run() {

        BlockSnapshot block = component.getBlock();

        if (block.getState().getType().equals(BlockTypes.WOODEN_BUTTON) || block.getState().getType().equals(BlockTypes.STONE_BUTTON)) {
            ComponentButton.power(block.getState(), false);
            MathUtil.forceUpdate(block.getLocation().get().getRelative(block.get(Keys.DIRECTION).get().getOpposite())
                    .createSnapshot());
        }

        activatedButtonMap.remove(block.getLocation());
    }
}
