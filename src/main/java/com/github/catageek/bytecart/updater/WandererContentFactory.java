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

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.file.BCFile;
import com.github.catageek.bytecart.file.BookFile;
import com.github.catageek.bytecart.updater.Wanderer.Level;
import com.github.catageek.bytecart.updater.Wanderer.Scope;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

abstract public class WandererContentFactory {

    public static WandererContent getWandererContent(Inventory inv)
            throws IOException, ClassNotFoundException {
        WandererContent rte = null;
        try (BookFile file = new BookFile(inv, 0, true)) {
            if (!file.isEmpty()) {
                ObjectInputStream ois = new ObjectInputStream(file.getInputStream());
                rte = (WandererContent) ois.readObject();
            }
        }
        rte.setInventory(inv);
        return rte;
    }

    public static boolean isWanderer(Inventory inv) {
        String prefix = getType(inv);
        if (prefix != null) {
            return ByteCartRedux.myPlugin.getWandererManager().isWandererType(prefix);
        }

        return false;
    }

    static String getType(Inventory inv) {
        ItemStack stack = inv.getItem(0);
        String prefix = null;
        if (stack != null && stack.getType().equals(Material.WRITTEN_BOOK) && stack.hasItemMeta()) {
            BookMeta book = (BookMeta) stack.getItemMeta();
            String booktitle = book.getTitle();
            int index = booktitle.indexOf(".");
            if (index > 0) {
                prefix = booktitle.substring(0, index);
            }
        }
        return prefix;
    }

    public static boolean isWanderer(Inventory inv, Scope scope) {
        return isWanderer(inv, scope, null, null);
    }

    public static boolean isWanderer(Inventory inv, Level level, String type) {
        return isWanderer(inv, level.scope, level.type, type);
    }

    public static boolean isWanderer(Inventory inv, String type) {
        return isWanderer(inv, null, null, type);
    }

    private static boolean isWanderer(Inventory inv, Scope scope, String suffix, String type) {
        if (!isWanderer(inv)) {
            return false;
        }
        ItemStack stack = inv.getItem(0);
        if (stack != null && stack.getType().equals(Material.WRITTEN_BOOK) && stack.hasItemMeta()) {
            final BookMeta book = (BookMeta) stack.getItemMeta();
            final String booktitle = book.getTitle();
            final String dot = "\\.";
            final StringBuilder match = new StringBuilder();

            match.append("^");

            final String alphanums = "[a-zA-Z]{1,}";

            if (type != null) {
                match.append(type).append(dot);
            } else {
                match.append(alphanums).append(dot);
            }

            if (scope != null) {
                match.append(scope.name).append(dot);
            } else {
                match.append(alphanums).append(dot);
            }

            if (suffix != null) {
                match.append(suffix).append(dot);
            }

            match.append(".*");
            if (BookFile.isBookFile(inv, 0)
                    && booktitle.matches(match.toString())) {
                return true;
            }
        }
        return false;
    }

    public static void createWanderer(Inventory inv, int region, Level level, Player player
            , String name, String type) throws IOException {
        try (BCFile file = new BookFile(inv, 0, true, name)) {
            String dot = ".";
            StringBuilder match = new StringBuilder();
            match.append(name).append(dot).append(level.scope.name);
            match.append(dot).append(type).append(dot);
            file.setDescription(match.toString());
            file.flush();
        }
    }

    public static <T extends InventoryContent> void saveContent(T rte)
            throws IOException, ClassNotFoundException {
        Inventory inv = rte.getInventory();

        try (BCFile file = new BookFile(inv, 0, true)) {
            file.clear();
            ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream());
            oos.writeObject(rte);
            oos.flush();
        }
    }

    public static void deleteContent(Inventory inv) {
        inv.clear(0);
    }
}
