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

import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BCWandererManager implements WandererManager {

    private static final Map<String, WandererFactory> map = new HashMap<>();

    /**
     * Register a wanderer type
     *
     * @param factory the wanderer class implementing the wanderer
     * @param type the name that will reference this type of wanderer
     */
    public boolean register(WandererFactory factory, String type) {
        if (map.containsKey(type)) {
            return false;
        }
        map.put(type, factory);
        return true;
    }

    /**
     * Turn a cart into a wanderer
     *
     * @param ivc the content of the wanderer
     * @param type the name of the type of wanderer previously registered
     * @param suffix a suffix to add to book title
     */
    public boolean create(InventoryContent ivc, String type, String suffix) {
        if (!map.containsKey(type)) {
            return false;
        }
        try {
            WandererContentFactory.createWanderer(ivc.getInventory(), ivc.getLevel(),
                    type, suffix);
            WandererContentFactory.saveContent(ivc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Get a wanderer
     *
     * @param inv the inventory where to extract the wanderercontent from
     * @return the wanderer
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public WandererFactory getFactory(CarriedInventory<?> inv) throws ClassNotFoundException, IOException {
        if (WandererContentFactory.isWanderer(inv)
                && WandererContentFactory.getWandererContent(inv) != null) {
            return map.get(WandererContentFactory.getType(inv));
        }
        return null;
    }

    /**
     * Tells if this type is registered as a wanderer type
     *
     * @param type the type to test
     * @return true if the type is registered
     */
    public boolean isWandererType(String type) {
        return map.containsKey(type);
    }

    public void saveContent(InventoryContent rte) throws IOException {

        WandererContentFactory.saveContent(rte);
    }

    @Override
    public void unregister(String name) {
        map.remove(name);
    }
}
