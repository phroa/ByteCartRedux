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

import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.hardware.RegistryOutput;
import com.github.catageek.bytecart.io.ComponentSign;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.util.DirectionRegistry;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

import java.io.IOException;

final class BC7009 extends AbstractTriggeredSign implements Triggerable {

    private final Direction from;

    BC7009(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        from = getCardinal().getOpposite();
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        this.addIO();
        RoundRobin();
    }

    private void RoundRobin() {
        final ComponentSign sign = new ComponentSign(this.getBlock());
        final String line = sign.getLine(3);
        DirectionRegistry dir;
        try {
            int current = Integer.parseInt(line);
            dir = new DirectionRegistry(current);
        } catch (NumberFormatException e) {
            dir = new DirectionRegistry(from);
        }
        Direction newdir = MathUtil.clockwise(dir.getBlockFace());
        if (newdir.equals(from)) {
            newdir = MathUtil.clockwise(newdir);
        }
        final int amount = new DirectionRegistry(newdir).getAmount();
        this.getOutput(0).setAmount(amount);
        sign.setLine(3, "" + amount);
    }

    @Override
    public String getName() {
        return "BC7009";
    }

    @Override
    public String getFriendlyName() {
        return "Load Balancer";
    }

    /**
     * Registers levers as output
     */
    private void addIO() {

        // Center of the device, at sign level
        BlockSnapshot center =
                this.getBlock().getLocation().get().add(this.getCardinal().toVector3d().mul(2)).getRelative(MathUtil.clockwise(this.getCardinal()))
                        .createSnapshot();

        // Main output
        OutputPin[] sortie = new OutputPin[4];
        // East
        sortie[0] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.WEST.toVector3d().mul(3)).getRelative(Direction.SOUTH).createSnapshot());
        // North
        sortie[1] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.EAST.toVector3d().mul(3)).getRelative(Direction.NORTH).createSnapshot());
        // South
        sortie[3] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.SOUTH.toVector3d().mul(3)).getRelative(Direction.EAST).createSnapshot());
        // West
        sortie[2] = OutputPinFactory
                .getOutput(center.getLocation().get().add(Direction.NORTH.toVector3d().mul(3)).getRelative(Direction.WEST).createSnapshot());

        RegistryOutput main = new PinRegistry<>(sortie);

        // output[0] is main levers
        this.addOutputRegistry(main);
    }

}
