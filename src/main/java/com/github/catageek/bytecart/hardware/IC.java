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
package com.github.catageek.bytecart.hardware;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

/**
 * Represents an IC, i.e an component with input and output
 */
public interface IC {

    /**
     * Return the name of the permission needed to create or destroy this IC
     *
     * @return the permission
     */
    String getBuildPermission();

    /**
     * Return the official name of this IC.
     *
     * @return the name
     */
    String getName();

    /**
     * Return the friendly name of this IC
     *
     * @return the friendly name
     */
    String getFriendlyName();

    /**
     * Return the tax assigned to this IC when used
     *
     * @return the tax
     */
    int getTriggertax();


    /**
     * Return the tax assigned to this IC when built
     *
     * @return the tax
     */
    int getBuildtax();

    /**
     * Get the block implementing this IC, usually the sign
     *
     * @return the block
     */
    BlockSnapshot getBlock();

    /**
     * Get the location of the IC, usually the location of the sign
     *
     * @return the location
     */
    Location getLocation();

    /**
     * Get the orientation of the sign
     *
     * @return the same direction as the player is facing when looking at the sign
     */
    Direction getCardinal();

    /**
     * Register an input for this IC
     *
     * @param reg the input to register
     */
    void addInputRegistry(RegistryInput reg);

    /**
     * Register an output for this IC
     *
     * @param reg the output to register
     */
    void addOutputRegistry(RegistryOutput reg);

    /**
     * Get an input of this IC
     *
     * @param index the index of the input to get
     * @return the input
     */
    RegistryInput getInput(int index);

    /**
     * Get an output of this IC
     *
     * @param index the index of the output to get
     * @return the output
     */
    RegistryOutput getOutput(int index);
}

