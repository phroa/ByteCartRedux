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
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.io.IOException;

public interface WandererFactory {

    /**
     * @return a new wanderer instance
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Wanderer getWanderer(BCSign bc, CarriedInventory<?> inv) throws ClassNotFoundException, IOException;

}
