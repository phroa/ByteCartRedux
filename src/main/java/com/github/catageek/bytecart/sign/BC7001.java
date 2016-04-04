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

import com.flowpowered.math.vector.Vector3d;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.RailDirection;
import org.spongepowered.api.data.type.RailDirections;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;

/**
 * this IC represents a stop/start block
 * it is commanded by a wire (like FalseBook 'station' block)
 * wire on => start or no velocity change
 * wire off => stop
 * it provides a busy bit with a lever on the block above the sign
 * lever off = block occupied and not powered
 * lever on = block free OR powered
 */
final class BC7001 extends AbstractTriggeredSign implements Triggerable, Powerable {

    /**
     * Constructor : !! vehicle can be null !!
     */
    BC7001(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
    public void trigger() {

        // add output occupied line = lever

        OutputPin[] lever = new OutputPin[1];

        // Right
        lever[0] = OutputPinFactory.getOutput(this.getBlock().getLocation().get().getRelative(getCardinal().getOpposite()).createSnapshot());

        // OutputRegistry[0] = occupied signal
        this.addOutputRegistry(new PinRegistry<>(lever));

        // here starts the action

        // is there a minecart above ?
        if (this.getVehicle() != null) {

            // add input command = redstone

            InputPin[] wire = new InputPin[2];

            // Right
            wire[0] = InputPinFactory.getInput(
                    this.getBlock().getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.clockwise(getCardinal())).createSnapshot());
            // left
            wire[1] = InputPinFactory.getInput(
                    this.getBlock().getLocation().get().getRelative(Direction.UP).getRelative(MathUtil.anticlockwise(getCardinal()))
                            .createSnapshot());

            // InputRegistry[0] = start/stop command
            this.addInputRegistry(new PinRegistry<>(wire));

            // if the wire is on
            if (this.getInput(0).getValue() > 0) {
                if (this.wasTrain(this.getLocation())) {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(this.getLocation());
                }
                if (this.isTrain()) {
                    this.setWasTrain(this.getLocation(), true);
                }

                // the lever is on too
                final BC7001 myBC7001 = this;

                Sponge.getScheduler().createTaskBuilder()
                        .delayTicks(6)
                        .execute(() -> {

                            // we set busy
                            myBC7001.getOutput(0).setAmount(1);

                        })
                        .submit(ByteCartRedux.myPlugin);


                // if the cart is stopped, start it
                if (this.getVehicle().getVelocity().equals(Vector3d.ZERO)) {
                    if (((Minecart) this.getVehicle()).getSwiftness() == 0) {
                        ((Minecart) this.getVehicle()).setSwiftness(0.4d);
                    }
                    this.getVehicle().setVelocity(
                            (this.getCardinal().toVector3d()).mul(ByteCartRedux.rootNode.getNode("BC7001", "startvelocity").getDouble()));
                }
            }

            // if the wire is off
            else {

                // stop the cart if this is not a train and tells to the previous block that we are stopped
                if (!this.wasTrain(getLocation())) {
                    // the lever is off
                    this.getOutput(0).setAmount(0);
                    this.getVehicle().setVelocity(Vector3d.ZERO);
                    ((Minecart) this.getVehicle()).setSwiftness(0d);
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap()
                            .remove(getBlock().getLocation().get().add(getCardinal().getOpposite().toVector3d().mul(2)));
                } else {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(this.getLocation());
                }

            }
            // if this is the first car of a train
            // we keep it during 2 s
        }

        // there is no minecart above
        else {
            // the lever is on
            this.getOutput(0).setAmount(1);
        }

    }

    @Override
    public void power() throws ClassNotFoundException, IOException {
        // power update

        Triggerable bc;

        // We need to find if a cart is stopped and set the member variable Vehicle
        BlockSnapshot block = this.getBlock().getLocation().get().add(Direction.UP.toVector3d().mul(2)).createSnapshot();
        Location<World> loc = block.getLocation().get();

        // if the rail is in slope, the cart is 1 block up
        if (block.supports(Keys.RAIL_DIRECTION) && ascending(block.getState().get(Keys.RAIL_DIRECTION).get())) {
            loc.add(0, 1, 0);
        }

        bc = TriggeredSignFactory.getTriggeredIC(this.getBlock(), MathUtil.getVehicleByLocation(loc));

        if (bc != null) {
            bc.trigger();
        } else {
            this.trigger();
        }
    }

    static boolean ascending(RailDirection d) {

        RailDirection[] sloped = {RailDirections.ASCENDING_EAST,
                RailDirections.ASCENDING_NORTH,
                RailDirections.ASCENDING_SOUTH,
                RailDirections.ASCENDING_WEST};

        for (RailDirection direction : sloped) {
            if (direction.equals(d)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final String getName() {
        return "BC7001";
    }

    @Override
    public final String getFriendlyName() {
        return "Stop/Start";
    }
}
