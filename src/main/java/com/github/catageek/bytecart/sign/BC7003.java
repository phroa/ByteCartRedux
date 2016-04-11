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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collection.ExpirableMap;
import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.hardware.RegistryOutput;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.thread.Expirable;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A cart counter
 */
final class BC7003 extends AbstractIC implements Triggerable, Powerable {

    private static final ExpirableMap<Location<World>, Integer> WAVECOUNT = new ExpirableMap<>(400, false, "BC7003");

    BC7003(BlockSnapshot block) {
        super(block);
    }

    BC7003(BlockSnapshot block, RegistryOutput io) {
        this(block);
        // forcing output[0] to be the one in parameter
        this.addOutputRegistry(io);
    }


    @Override
    public void trigger() {

        // adding lever as output 0 (if not forced in constructor)
        this.AddOutputIO();

        // We treat the counter
        try {


            if (!this.decrementWaveCount()) {
                (new RemoveCount(ByteCartRedux.rootNode.getNode("sign", "bc7003", "lockduration").getInt() + 6, true, "Removecount"))
                        .reset(getLocation(), this.getOutput(0));
            }
        } catch (Exception e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }

            e.printStackTrace();
        }
    }

    @Override
    public void power() {
        // check if we are really powered
        if (!this.getBlock().getLocation().get().getRelative(MathUtil.clockwise(getCardinal())).createSnapshot().getState().get(Keys.POWERED)
                .orElse(false) && !this.getBlock().getLocation().get().getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot().getState()
                .get(Keys.POWERED).orElse(false)) {
            return;
        }

        // add input command = redstone

        InputPin[] wire = new InputPin[2];

        // Right
        wire[0] = InputPinFactory.getInput(this.getBlock().getLocation().get().getRelative(Direction.UP).createSnapshot().getLocation().get()
                .getRelative(MathUtil.clockwise(getCardinal())).createSnapshot());
        // left
        wire[1] = InputPinFactory.getInput(this.getBlock().getLocation().get().getRelative(Direction.UP).createSnapshot().getLocation().get()
                .getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot());

        // InputRegistry[0] = detector
        this.addInputRegistry(new PinRegistry<>(wire));

        // Adding lever as output 0
        this.AddOutputIO();

        // if detector is on, the signal is red (= on)
        if (this.getInput(0).getValue() != 0) {

            // setting red signal
            this.getOutput(0).setAmount(1);

            this.incrementWaveCount();
            (new RemoveCount(400, true, "Removecount")).reset(getLocation(), this.getOutput(0));
            WAVECOUNT.reset(getLocation(), this.getOutput(0));
        }

    }

    /**
     * increment the counter
     *
     */
    private void incrementWaveCount() {
        synchronized (WAVECOUNT) {
            if (!WAVECOUNT.contains(this.getLocation())) {
                WAVECOUNT.put(getLocation(), 1);
            } else {
                WAVECOUNT.put(getLocation(), WAVECOUNT.get(getLocation()) + 1);
            }
        }


    }

    /**
     * decrement the counter
     *
     * @return true if the counter is strictly positive
     */
    private boolean decrementWaveCount() {
        synchronized (WAVECOUNT) {
            if (WAVECOUNT.contains(getLocation()) && WAVECOUNT.get(getLocation()) > 1) {
                WAVECOUNT.put(getLocation(), WAVECOUNT.get(getLocation()) - 1);
            } else {
                WAVECOUNT.remove(getLocation());
                return false;
            }
            return true;
        }
    }

    /**
     * Add the lever behind the sign to give the red light signal
     *
     */
    private void AddOutputIO() {
        // Declare red light signal = lever

        OutputPin[] lever = new OutputPin[1];

        // Right
        lever[0] = OutputPinFactory.getOutput(this.getBlock().getLocation().get().add(getCardinal().toVector3d().mul(2)).createSnapshot());

        // OutputRegistry = red light signal
        this.addOutputRegistry(new PinRegistry<>(lever));
    }

    @Override
    public final String getName() {
        return "BC7003";
    }

    @Override
    public final String getFriendlyName() {
        return "Cart Counter";
    }

    @Override
    public boolean isTrain() {
        return false;
    }

    @Override
    public boolean wasTrain(Location<World> loc) {
        return false;
    }

    @Override
    public boolean isLeverReversed() {
        return false;
    }

    /**
     * Runnable to remove the counter after a timeout
     */
    private final class RemoveCount extends Expirable<Location<World>> {

        public RemoveCount(long duration, boolean isSync, String name) {
            super(duration, isSync, name);
        }

        @Override
        public void expire(Object... objects) {
            ((RegistryOutput) objects[0]).setAmount(0);
        }
    }
}


