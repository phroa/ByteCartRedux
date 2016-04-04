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

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.routing.RoutingTable;
import com.github.catageek.bytecart.sign.BCRouter;
import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

import java.util.Random;

/**
 *
 * This class represents a Wanderer. All wanderers must extends this class.
 * Wanderers implementors would prefer to override an existing default Wanderer implementation
 * such as DefaultRouterWanderer.
 *
 */
public abstract class AbstractWanderer {

    private final BCSign bcsign;
    private final DirectionRegistry from;
    private final int region;
    private Address signAddress;

    /**
     * @param bc the ic that triggers this wanderer
     * @param region the region where this wanderer is attached to
     */
    AbstractWanderer(BCSign bc, int region) {
        bcsign = bc;
        signAddress = bc.getSignAddress();
        this.region = region;

        if (bc instanceof BCRouter) {
            BCRouter ic = (BCRouter) bc;
            from = new DirectionRegistry(ic.getFrom());
        } else {
            from = null;
        }
    }

    /**
     * Get a random route that is not from where we are coming
     *
     * @param routingTable the routing table where to pick up a route
     * @param from the direction from where we are coming
     * @return the direction
     */
    public static Direction getRandomBlockFace(RoutingTable routingTable, Direction from) {

        // selecting a random destination avoiding ring 0 or where we come from
        DirectionRegistry direction = new DirectionRegistry(1 << (new Random()).nextInt(4));

        while (direction.getBlockFace() == from || routingTable.isDirectlyConnected(0, direction)) {
            direction.setAmount(1 << (new Random()).nextInt(4));
        }

        return direction.getBlockFace();
    }

    /**
     * Method that must return the direction to take on a BC8XXX sign
     *
     * @return the direction that the cart should take
     */
    public abstract Direction giveRouterDirection();

    /**
     * Method that must return the position of the lever
     *
     * @return the position of the lever
     */
    public abstract Side giveSimpleDirection();

    /**
     * Method called when an updater meets a BC8XXX sign
     *
     * @param To the direction where the cart goes
     */
    public abstract void doAction(Direction To);

    /**
     * Method called when an updater meets a BC9XXX sign
     *
     * @param To the position of the lever
     */
    public abstract void doAction(Side To);

    /**
     * Tells if we are at the border of a region
     *
     * @return true if we just met a backbone router, or leave the backbone
     */
    final boolean isAtBorder() {

        return this.getWandererRegion() == 0 ^ this.getSignLevel().scope.equals(Wanderer.Scope.BACKBONE);

    }

    /**
     * @return the address on the sign
     */
    final Address getSignAddress() {
        return signAddress;
    }

    /**
     * Set the member variable signAddress
     */
    final void setSignAddress(Address signAddress) {
        this.signAddress = signAddress;
    }

    /**
     * @return the direction from where we are coming
     */
    @SuppressWarnings("WeakerAccess")
    public final DirectionRegistry getFrom() {
        return from;
    }

    /**
     * @return the level of the sign
     */
    final Wanderer.Level getSignLevel() {
        return this.getBcSign().getLevel();
    }

    /**
     * @return the Vehicle
     */
    public final Entity getVehicle() {
        return this.getBcSign().getVehicle();
    }

    /**
     * Tells if we are about to make a U-turn
     *
     * @param to the direction we want to go
     * @return true if we make a U-turn
     */
    final boolean isSameTrack(Direction to) {
        return getFrom().getBlockFace().equals(to);
    }

    /**
     * Get the region where this wanderer is attached to
     *
     * @return the region number
     */
    @SuppressWarnings("WeakerAccess")
    public final int getWandererRegion() {
        return region;
    }

    /**
     * Get the center of the IC that triggers this wanderer
     *
     * @return the center
     */
    @SuppressWarnings("WeakerAccess")
    public final BlockSnapshot getCenter() {
        return this.getBcSign().getCenter();
    }

    /**
     * Get the name of the sign
     *
     * @return the name
     */
    public final String getFriendlyName() {
        return this.getBcSign().getFriendlyName();
    }

    /**
     * @return the IC
     */
    @SuppressWarnings("WeakerAccess")
    public final BCSign getBcSign() {
        return bcsign;
    }
}