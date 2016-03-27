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
package com.github.catageek.bytecart.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;
import org.spongepowered.api.util.Direction;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


public final class MathUtil {

    public static final BlockFace clockwise(BlockFace f) {
        BlockFace b = MathUtil.straightUp(f);
        switch (b) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.NORTH;
            default:
                break;
        }
        return b;

    }

    public static final BlockFace anticlockwise(BlockFace f) {
        BlockFace b = MathUtil.straightUp(f);
        switch (b) {
            case NORTH:
                return BlockFace.WEST;
            case EAST:
                return BlockFace.NORTH;
            case SOUTH:
                return BlockFace.EAST;
            case WEST:
                return BlockFace.SOUTH;
            default:
                break;
        }
        return b;

    }

    public static final Direction straightUp(Direction b) {
        switch (b) {
            case NORTH:
            case NORTH_NORTHWEST:
            case NORTH_NORTHEAST:
                return Direction.NORTH;
            case EAST:
            case EAST_NORTHEAST:
            case EAST_SOUTHEAST:
                return Direction.EAST;
            case SOUTH:
            case SOUTH_SOUTHWEST:
            case SOUTH_SOUTHEAST:
                return Direction.SOUTH;
            case WEST:
            case WEST_NORTHWEST:
            case WEST_SOUTHWEST:
                return Direction.WEST;
            default:
                return Direction.UP;
        }
    }

    public static final void forceUpdate(Block b) {
        Material oldData = b.getType();
        b.setType(Material.BEDROCK);
        b.setType(oldData);
    }

    public static final void loadChunkAround(World world, int x, int z, int radius) {
        int j, i = x - radius, k = x + radius, l = z + radius;
        for (; i <= k; ++i) {
            for (j = z - radius; j <= l; ++j) {
                world.loadChunk(i, j, false);
            }
        }

    }

    /**
     * Get the vehicle that is at specific location
     *
     * @param loc the location
     * @return the vehicle, or null
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static org.bukkit.entity.Vehicle getVehicleByLocation(Location loc)
            throws ClassNotFoundException, IOException {
        List<Entity> ent = Arrays.asList(loc.getBlock().getChunk().getEntities());
        for (ListIterator<Entity> it = ent.listIterator(); it.hasNext(); ) {
            if (it.next() instanceof Minecart) {
                it.previous();

                Location cartloc = ((Minecart) it.next()).getLocation();

                if (cartloc.getBlockX() == loc.getBlockX() && cartloc.getBlockZ() == loc.getBlockZ()) {
                    it.previous();
                    return (Vehicle) it.next();
                }
            }
        }
        return null;
    }

    public static double getSpeed(final Minecart minecart) {

        final Vector velocity = minecart.getVelocity();

        if (velocity.getX() > 0) {
            return velocity.getX();
        } else if (velocity.getX() < 0) {
            return -velocity.getX();
        } else if (velocity.getZ() > 0) {
            return velocity.getZ();
        } else if (velocity.getZ() < 0) {
            return -velocity.getZ();
        } else {
            return 0;
        }
    }

    public static void setSpeed(final Minecart minecart, final double speed) {

        final Vector velocity = minecart.getVelocity();

        if (velocity.getX() > 0) {
            velocity.setX(speed);
        } else if (velocity.getX() < 0) {
            velocity.setX(-speed);
        } else if (velocity.getZ() > 0) {
            velocity.setZ(speed);
        } else if (velocity.getZ() < 0) {
            velocity.setZ(-speed);
        }

        minecart.setVelocity(velocity);
    }
}