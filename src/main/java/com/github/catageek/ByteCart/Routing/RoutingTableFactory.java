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
package com.github.catageek.ByteCart.Routing;

import com.github.catageek.ByteCart.FileStorage.BookFile;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * Factory for routing tables
 */
public final class RoutingTableFactory {

    /**
     * Get a routing table
     *
     * @param inv the inventory to open
     * @return the RoutingTableWritable object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static public RoutingTableWritable getRoutingTable(Inventory inv) throws IOException, ClassNotFoundException {
        RoutingTableBook rt;

        // If upgrading from ByteCartRedux 1.x, cleaning routing table
        if (!inv.contains(Material.WRITTEN_BOOK)) {
            inv.clear();
        }

        try (BookFile file = new BookFile(inv, 0, true, "RoutingTableWritable")) {
            if (file.isEmpty()) {
                return new RoutingTableBook(inv);
            }
            ObjectInputStream ois = new ObjectInputStream(file.getInputStream());
            rt = (RoutingTableBook) ois.readObject();
        }
        rt.setInventory(inv);
        return rt;
    }

}
