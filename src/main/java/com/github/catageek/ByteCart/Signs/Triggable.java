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
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.HAL.IC;
import org.bukkit.Location;

import java.io.IOException;

/**
 * An IC that can be triggered by a cart should implement this
 */
public interface Triggable extends IC {

    /**
     * Method called when a cart is passing on the IC
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void trigger() throws ClassNotFoundException, IOException;

    /**
     * Tell if the cart that triggers this IC has the train bit set, i.e is the head of a train
     *
     * @return true if the train bit is set
     */
    public boolean isTrain();

    /**
     * Tell if the IC was previously triggered by a cart with the train bit set
     *
     * This method retrieves the persistent value stored in a map.
     *
     * The value retrieved has a timeout, i.e the method will return false after a while.
     *
     * @param loc the location of the IC
     * @return true if a train is currently using the IC
     */
    public boolean wasTrain(Location loc);

    /**
     * Tell if the lever is negated
     * @return true if it is negated
     */
    public boolean isLeverReversed();
}
