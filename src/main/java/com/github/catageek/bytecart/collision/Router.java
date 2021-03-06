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
package com.github.catageek.bytecart.collision;

import com.github.catageek.bytecart.hardware.RegistryOutput;
import org.spongepowered.api.util.Direction;

/**
 * A router representation in the collision management layer
 */
public interface Router extends CollisionAvoider {

    /**
     * Ask for a direction, requesting a possible transition
     *
     * @param from the direction from where comes the cart
     * @param to the direction where the cart goes to
     * @param isTrain true if it is a train
     * @return the direction actually taken
     */
    <T extends Router> Direction wishToGo(Direction from, Direction to, boolean isTrain);

    /**
     * book the router, i.e mark it as currently in use
     *
     * @param b true if this is a train
     */
    void book(boolean b);

    int getSecondpos();

    /**
     * Set the value of the position of the 8 exterior levers, starting from the origin axis.
     *
     * <p>a bit to 1 means the lever is on, a bit to 0 means the lever is off</p>
     *
     * @param secondpos the value to set
     */
    void setSecondpos(int secondpos);

    /**
     * Get the mask of the current usage of the router, starting from the origin axis.
     *
     * <p>a bit to 1 means the lever is blocked, a bit to 0 means the lever is not blocked</p>
     *
     * @return the mask
     */
    int getPosmask();

    /**
     * Set the mask of the current usage of the router, starting from the origin axis.
     *
     * <p>a bit to 1 means the lever is blocked, a bit to 0 means the lever is not blocked</p>
     *
     * @param posmask the mask to set
     */
    void setPosmask(int posmask);

    /**
     * Get the direction from where the cart is coming.
     *
     * <p>This direction is also the origin axis of the registries</p>
     *
     *
     * @return the direction
     */
    Direction getFrom();

    /**
     * Activate levers according to registry
     *
     *
     * @param from the origin direction
     */
    void route(Direction from);

    /**
     * Get the lever registry
     *
     * @param i 0 for main levers, 1 for secondary levers
     * @return the value of the registry
     */
    RegistryOutput getOutput(int i);

    /**
     * Get the direction where the cart eventually go
     *
     *
     * @return the direction
     */
    Direction getTo();
}
