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

import com.github.catageek.bytecart.sign.BCSign;
import com.github.catageek.bytecart.updater.Wanderer.Level;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public final class UpdaterFactory implements WandererFactory {

    public Wanderer getWanderer(BCSign bc, Inventory inv) throws ClassNotFoundException, IOException {
        UpdaterContent rte;
        if (WandererContentFactory.isWanderer(inv, Level.REGION, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterRegion(bc, rte);
        }

        if (WandererContentFactory.isWanderer(inv, Level.RESET_REGION, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterResetRegion(bc, rte);
        }

        if (WandererContentFactory.isWanderer(inv, Level.BACKBONE, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterBackBone(bc, rte);
        }

        if (WandererContentFactory.isWanderer(inv, Level.RESET_BACKBONE, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterResetBackbone(bc, rte);
        }

        if (WandererContentFactory.isWanderer(inv, Level.RESET_LOCAL, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterResetLocal(bc, rte);
        }

        if (WandererContentFactory.isWanderer(inv, Level.LOCAL, "Updater")
                && (rte = UpdaterContentFactory.getUpdaterContent(inv)) != null) {
            return new UpdaterLocal(bc, rte);
        }
        return null;
    }
}
