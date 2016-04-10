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
package com.github.catageek.bytecart;

import com.github.catageek.bytecart.updater.WandererManager;
import org.slf4j.Logger;

import java.io.File;


public interface ByteCartPlugin {

    /**
     * Get the logger
     *
     * @return the logger
     */
    Logger getLog();

    /**
     * @return the wanderer factory
     */
    WandererManager getWandererManager();

    /**
     * @return the folder to save databases in
     */
    File getDataFolder();
}
