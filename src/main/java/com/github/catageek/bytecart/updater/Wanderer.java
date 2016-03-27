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
package com.github.catageek.bytecart.updater;

import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.hardware.IC;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Vehicle;

/**
 * Represents an updater
 */
public interface Wanderer {

    /**
     * Method called when an updater meets a BC9XXX sign
     *
     * @param to the position of the lever
     */
    void doAction(Side to);

    /**
     * Method called when an updater meets a BC8XXX sign
     *
     * @param to the direction where the cart goes
     */
    void doAction(BlockFace to);

    /**
     * Method that must return the direction to take on a BC8XXX sign
     *
     * @return the direction that the cart should take
     */
    BlockFace giveRouterDirection();

    /**
     * Method that must return the position of the lever
     *
     * @return the position of the lever
     */
    Side giveSimpleDirection();

    /**
     * Get the current track where the updater is
     *
     * @return the track
     */
    int getTrackNumber();

    /**
     * Get the direction from where the cart is coming
     *
     * @return the direction, or null if not relevant
     */
    DirectionRegistry getFrom();

    /**
     * Get the type of updater
     *
     * @return the type
     */
    Level getLevel();

    /**
     * Get the Vehicle that this updater is
     *
     * @return the Vehicle
     */
    Vehicle getVehicle();

    /**
     * Get the region where this updater is attached to
     *
     * @return the region
     */
    int getWandererRegion();

    /**
     * Get the center of the IC that triggered this updater class
     *
     * @return the center block
     */
    Block getCenter();

    /**
     * Get the name of the sign
     *
     * @return the name
     */
    String getFriendlyName();

    /**
     * Get the IC that triggered this updater
     *
     * @return the IC
     */
    IC getBcSign();

    /**
     * The scope of the updater
     */
    enum Scope {
        BACKBONE("backbone"),
        REGION("region"),
        LOCAL("local");

        public final String name;

        Scope(String name) {
            this.name = name;
        }

    }

    /**
     * All types of updaters
     */
    enum Level {
        BACKBONE("backbone", 0, Scope.BACKBONE, "rip"),
        REGION("region", 1, Scope.REGION, "rip"),
        LOCAL("local", 2, Scope.LOCAL, "conf"),
        OTHER_BACKBONE("other_backbone", 3, Scope.BACKBONE, "other"),
        OTHER_REGION("other_region", 4, Scope.REGION, "other"),
        OTHER_LOCAL("other_local", 5, Scope.LOCAL, "other"),
        RESET_BACKBONE("reset_backbone", 8, Scope.BACKBONE, "reset"),
        RESET_REGION("reset_region", 9, Scope.REGION, "reset"),
        RESET_LOCAL("reset_local", 10, Scope.LOCAL, "reset");

        public final int number;
        public final String name;
        public final Scope scope;
        public final String type;

        Level(String name, int level, Scope scope, String type) {
            this.number = level;
            this.name = name;
            this.scope = scope;
            this.type = type;
        }

        static public boolean isMember(String aName) {
            Level[] aLevels = Level.values();
            for (Level aLevel : aLevels) {
                if (aLevel.name.equals(aName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
