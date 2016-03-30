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

import org.spongepowered.api.item.inventory.Inventory;

import java.io.IOException;

public interface WandererManager {

    /**
     * Register a wanderer factory
     *
     * @param wanderer the wanderer class implementing the wanderer
     * @param name the name that will reference this type of wanderer
     */
    boolean register(WandererFactory wanderer, String name);

    /**
     * Unregister a wanderer factory. All wanderers in the network
     * that were created with this factory will be treated as normal carts.
     *
     * @param name the name of the type of wanderer
     */
    void unregister(String name);

    /**
     * Create a wanderer
     *
     * @param ivc the content of the wanderer
     * @param name the name of the type of wanderer previously registered
     * @param type a suffix to add to book title
     */
    boolean create(InventoryContent ivc, String name, String type);

    /**
     * Get a wanderer factory
     *
     * @param bc the sign that request the wanderer
     * @param inv the inventory where to extract the wanderercontent from
     * @return the wanderer factory
     * @throws ClassNotFoundException
     * @throws IOException
     */
    WandererFactory getFactory(Inventory inv) throws ClassNotFoundException, IOException;

    void saveContent(InventoryContent rte) throws ClassNotFoundException, IOException;
}
