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
package com.github.catageek.ByteCart.HAL;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Represents an IC, i.e an component with input and output
 */
public interface IC {

    /**
     * Return the name of the permission needed to create or destroy this IC
     *
     * @return the permission
     */
    public String getBuildPermission();

    /**
     * Return the official name of this IC.
     *
     * @return the name
     */
    public String getName();

    /**
     * Return the friendly name of this IC
     *
     * @return the friendly name
     */
    public String getFriendlyName();

    /**
     * Return the tax assigned to this IC when used
     *
     * @return the tax
     */
    public int getTriggertax();


    /**
     * Return the tax assigned to this IC when built
     *
     * @return the tax
     */
    public int getBuildtax();

    /**
     * Get the block implementing this IC, usually the sign
     *
     * @return the block
     */
    public Block getBlock();

    /**
     * Get the location of the IC, usually the location of the sign
     *
     * @return the location
     */
    public Location getLocation();

    /**
     * Get the orientation of the sign
     *
     * @return the same direction as the player is facing when looking at the sign
     */
    public BlockFace getCardinal();

    /**
     * Register an input for this IC
     *
     * @param reg the input to register
     */
    public void addInputRegistry(RegistryInput reg);

    /**
     * Register an output for this IC
     *
     * @param reg the output to register
     */
    public void addOutputRegistry(RegistryOutput reg);

    /**
     * Get an input of this IC
     *
     * @param index the index of the input to get
     * @return the input
     */
    public RegistryInput getInput(int index);

    /**
     * Get an output of this IC
     *
     * @param index the index of the output to get
     * @return the output
     */
    public RegistryOutput getOutput(int index);
}

