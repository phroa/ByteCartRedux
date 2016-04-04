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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

abstract public class WandererContentFactory {

    public static WandererContent getWandererContent(CarriedInventory<?> inv)
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
        return prefix != null && ByteCartRedux.myPlugin.getWandererManager().isWandererType(prefix);

    }

    static String getType(Inventory inv) {
        Optional<ItemStack> stack = inv.query(new SlotIndex(0)).peek();
        String prefix = null;
        if (stack.isPresent() && stack.get().getItem().equals(ItemTypes.WRITTEN_BOOK)) {
            ItemStack book = stack.get();
            String booktitle = book.get(Keys.DISPLAY_NAME).get().toPlain();
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
        Optional<ItemStack> stack = inv.query(new SlotIndex(0)).peek();
        if (stack.isPresent() && stack.get().getItem().equals(ItemTypes.WRITTEN_BOOK)) {
            final ItemStack book = stack.get();
            final String booktitle = book.get(Keys.DISPLAY_NAME).get().toPlain();
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

    public static void createWanderer(CarriedInventory<?> inv, Level level, String name, String type) throws IOException {
        try (BCFile file = new BookFile(inv, 0, true, name)) {
            String dot = ".";
            file.setDescription(String.format("%s.%s.%s.", name, level.scope.name, type));
            file.flush();
        }
    }

    public static <T extends InventoryContent> void saveContent(T rte) throws IOException {
        CarriedInventory<?> inv = rte.getInventory();

        try (BCFile file = new BookFile(inv, 0, true)) {
            file.clear();
            ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream());
            oos.writeObject(rte);
            oos.flush();
        }
    }

    public static void deleteContent(Inventory inv) {
        inv.query(new SlotIndex(0)).clear();
    }
}
