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
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.BooleanTraits;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A button
 */
public class ComponentButton extends AbstractComponent implements OutputPin, InputPin {

    private static final Map<Location<World>, UUID> ACTIVATED_BUTTON_MAP = new ConcurrentHashMap<>();

    /**
     * @param block the block containing the component
     */
    ComponentButton(BlockSnapshot block) {
        super(block);
    }

    static void power(BlockState blockstate, boolean power) {
        if (blockstate.getType().equals(BlockTypes.STONE_BUTTON)) {
            blockstate.withTrait(BooleanTraits.STONE_BUTTON_POWERED, power);
        }
        if (blockstate.getType().equals(BlockTypes.WOODEN_BUTTON)) {
            blockstate.withTrait(BooleanTraits.WOODEN_BUTTON_POWERED, power);
        }
    }

    private static boolean buttonPowered(BlockState blockstate) {
        return blockstate.getTraitValue(BooleanTraits.STONE_BUTTON_POWERED).orElse(false) || blockstate
                .getTraitValue(BooleanTraits.WOODEN_BUTTON_POWERED).orElse(false);
    }

    @Override
    public void write(boolean bit) {
        final BlockSnapshot block = this.getBlock();
        final BlockState blockstate = block.getState();
        if (buttonPowered(blockstate)) {
            final ComponentButton component = this;
            UUID taskId;

            if (bit) {
                if (ACTIVATED_BUTTON_MAP.containsKey(block.getLocation().get())) {

                    // if button is already on, we cancel the scheduled thread
                    Sponge.getScheduler().getTaskById(ACTIVATED_BUTTON_MAP.get(block.getLocation().get())).get().cancel();

                    // and we reschedule one
                    taskId = Sponge.getScheduler().createTaskBuilder()
                            .delayTicks(40)
                            .execute(new SetButtonOff(component, ACTIVATED_BUTTON_MAP))
                            .submit(ByteCartRedux.myPlugin).getUniqueId();

                    // We update the HashMap
                    ACTIVATED_BUTTON_MAP.put(block.getLocation().get(), taskId);

                } else {
                    // if button is off, we power the button
                    power(blockstate, true);

                    MathUtil.forceUpdate(this.getBlock().getLocation().get().getRelative(this.getBlock().get(Keys.DIRECTION).get().getOpposite())
                            .createSnapshot());
                    taskId = Sponge.getScheduler().createTaskBuilder()
                            .delayTicks(40)
                            .execute(new SetButtonOff(component, ACTIVATED_BUTTON_MAP))
                            .submit(ByteCartRedux.myPlugin).getUniqueId();

                    // We update the HashMap
                    ACTIVATED_BUTTON_MAP.put(block.getLocation().get(), taskId);
                }
            }

        }
    }

    @Override
    public boolean read() {
        return buttonPowered(getBlock().getState());
    }


}
