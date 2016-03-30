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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Vehicle;
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
    private final DirectionRegistry From;
    private final int Region;
    private Address SignAddress;

    /**
     * @param bc the ic that triggers this wanderer
     * @param region the region where this wanderer is attached to
     */
    protected AbstractWanderer(BCSign bc, int region) {
        bcsign = bc;
        SignAddress = bc.getSignAddress();
        Region = region;

        if (bc instanceof BCRouter) {
            BCRouter ic = (BCRouter) bc;
            From = new DirectionRegistry(ic.getFrom());
        } else {
            From = null;
        }
    }

    /**
     * Get a random route that is not from where we are coming
     *
     * @param routingTable the routing table where to pick up a route
     * @param from the direction from where we are coming
     * @return the direction
     */
    public static final Direction getRandomBlockFace(RoutingTable routingTable, Direction from) {

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
    public abstract BlockFace giveRouterDirection();

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
    public abstract void doAction(BlockFace To);

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
    protected final boolean isAtBorder() {

        return this.getWandererRegion() == 0 ^ this.getSignLevel().scope.equals(Wanderer.Scope.BACKBONE);

    }

    /**
     * @return the address on the sign
     */
    protected final Address getSignAddress() {
        return SignAddress;
    }

    /**
     * Set the member variable SignAddress
     *
     * @param signAddress
     */
    protected final void setSignAddress(Address signAddress) {
        SignAddress = signAddress;
    }

    /**
     * @return the direction from where we are coming
     */
    public final DirectionRegistry getFrom() {
        return From;
    }

    /**
     * @return the level of the sign
     */
    public final Wanderer.Level getSignLevel() {
        return this.getBcSign().getLevel();
    }

    /**
     * @return the Vehicle
     */
    public final Vehicle getVehicle() {
        return this.getBcSign().getVehicle();
    }

    /**
     * Tells if we are about to make a U-turn
     *
     * @param To the direction we want to go
     * @return true if we make a U-turn
     */
    protected final boolean isSameTrack(BlockFace To) {
        return getFrom().getBlockFace().equals(To);
    }

    /**
     * Get the region where this wanderer is attached to
     *
     * @return the region number
     */
    public final int getWandererRegion() {
        return Region;
    }

    /**
     * Get the center of the IC that triggers this wanderer
     *
     * @return the center
     */
    public final Block getCenter() {
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
    public final BCSign getBcSign() {
        return bcsign;
    }
}