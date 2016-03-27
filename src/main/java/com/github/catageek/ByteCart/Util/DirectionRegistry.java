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
package com.github.catageek.ByteCart.Util;

import com.github.catageek.ByteCart.HAL.Registry;
import com.github.catageek.ByteCart.HAL.RegistryOutput;
import com.github.catageek.ByteCart.HAL.VirtualRegistry;
import com.github.catageek.ByteCart.Storage.Partitionable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.block.BlockFace;

/**
 * A 4-bit registry with 1 cardinal direction per bit
 */
public final class DirectionRegistry implements Partitionable {

    /**
     *
     */
    private VirtualRegistry registry;

	/* 
     * bit 3 : SOUTH
	 * bit 2 : WEST
	 * bit 1 : NORTH
	 * bit 0 : EASt
	 */

    public DirectionRegistry() {
        this.registry = new VirtualRegistry(4);
    }

    /**
     * Build the registry with an initial value
     *
     * @param value initial value
     */
    public DirectionRegistry(int value) {
        this();
        this.registry.setAmount(value);
    }

    /**
     * Build the registry matching the blockface
     *
     * @param b blockface
     */
    public DirectionRegistry(BlockFace b) {
        this();
        this.setCardinal(b, true);
    }

    /**
     * Set a cardinal direction bit individually
     *
     * @param face the cardinal direction
     * @param value true or false
     */
    public final void setCardinal(BlockFace face, boolean value) {

        switch (face) {
            case EAST:
                this.registry.setBit(0, value);
                break;
            case NORTH:
                this.registry.setBit(1, value);
                break;
            case WEST:
                this.registry.setBit(2, value);
                break;
            case SOUTH:
                this.registry.setBit(3, value);
            default:
                break;
        }

    }

    public final BlockFace ToString() {
        switch (this.getAmount()) {
            case 1:
                return BlockFace.SOUTH;
            case 2:
                return BlockFace.WEST;
            case 4:
                return BlockFace.NORTH;
            case 8:
                return BlockFace.EAST;
        }
        return BlockFace.SELF;
    }

    /**
     * Return the cardinal direction represented by this registry
     *
     * @return the direction, or self if there is no direction, or several directions are mixed
     */
    public final BlockFace getBlockFace() {
        switch (this.getAmount()) {
            case 1:
                return BlockFace.SOUTH;
            case 2:
                return BlockFace.WEST;
            case 4:
                return BlockFace.NORTH;
            case 8:
                return BlockFace.EAST;
        }
        return BlockFace.SELF;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Storage.Partitionable#getAmount()
     */
    @Override
    public final int getAmount() {
        return this.registry.getAmount();
    }

    /**
     * Get the value of the registry
     *
     * @param value the value
     */
    public final void setAmount(int value) {
        this.registry.setAmount(value);
    }

    /**
     * Return the registry
     *
     * @return the registry
     */
    public final Registry getRegistry() {
        RegistryOutput reg = new VirtualRegistry(4);
        reg.setAmount(this.getAmount());
        return reg;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getAmount();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof DirectionRegistry)) {
            return false;
        }

        DirectionRegistry rhs = (DirectionRegistry) o;

        return new EqualsBuilder().append(getAmount(), rhs.getAmount()).isEquals();
    }
}
