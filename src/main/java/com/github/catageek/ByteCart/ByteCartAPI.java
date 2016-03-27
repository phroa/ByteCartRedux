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
import org.slf4j.Logger;


public final class ByteCartAPI {

    private static ByteCartPlugin plugin;

    /**
     * @return the plugin
     */
    public static ByteCartPlugin getPlugin() {
        return plugin;
    }

    /**
     * @param plugin the plugin to set
     */
    public static void setPlugin(ByteCartPlugin plugin) {
        if (ByteCartAPI.plugin != null && plugin != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Plugin");
        }

        ByteCartAPI.plugin = plugin;
    }

    /**
     * @return the resolver registered
     */
    public static Resolver getResolver() {
        return plugin.getResolver();
    }

    /**
     * Set the resolver that will be used
     *
     * @param resolver the resolver provided
     */
    public static void setResolver(Resolver resolver) {
        plugin.setResolver(resolver);
    }

    /**
     * Get the logger
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return plugin.getLog();
    }

}
