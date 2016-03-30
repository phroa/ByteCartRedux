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

import com.github.catageek.bytecart.routing.RoutingTable;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;

import java.io.Serializable;
import java.util.Stack;


public interface InventoryContent extends Serializable {

    /**
     * @return the inventory
     */
    Inventory getInventory();

    /**
     * Get the level of the wanderer
     *
     * @return the level
     */
    Wanderer.Level getLevel();

    /**
     * Get the region of the wanderer
     *
     * @return the region
     */
    int getRegion();

    /**
     * @return the player that creates the wanderer
     */
    Player getPlayer();

    Counter getCounter();

    Stack<Integer> getStart();

    Stack<Integer> getEnd();

    boolean hasRouteTo(int ring);

    int getMinDistanceRing(
            RoutingTable routingTable,
            DirectionRegistry from);

    int getCurrent();

    void setCurrent(int i);

    int getMetric(int ring);

    void setRoute(int ring, int i);

}
