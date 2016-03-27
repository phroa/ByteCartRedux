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
package com.github.catageek.ByteCart;

import com.github.catageek.ByteCart.AddressLayer.Resolver;
import com.github.catageek.ByteCart.Wanderer.WandererManager;
import org.slf4j.Logger;

import java.io.File;


public interface ByteCartPlugin {

    /**
     * @return the resolver registered
     */
    public Resolver getResolver();

    /**
     * Set the resolver that will be used
     *
     * @param resolver the resolver provided
     */
    public void setResolver(Resolver resolver);

    /**
     * Get the logger
     *
     * @return the logger
     */
    public Logger getLog();

    /**
     * @return the wanderer factory
     */
    public WandererManager getWandererManager();

    /**
     * @return the folder to save databases in
     */
    public File getDataFolder();
}
