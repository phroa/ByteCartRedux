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

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.AddressLayer.AddressFactory;
import com.github.catageek.ByteCart.CollisionManagement.IntersectionSide;
import com.github.catageek.ByteCart.HAL.RegistryBoth;
import com.github.catageek.ByteCart.HAL.RegistryInput;
import com.github.catageek.ByteCart.Wanderer.WandererContentFactory;


/**
 * Match IP ranges.
 *
 * Example sign content:
 * 1. Empty
 * 2. [BCxxxx]
 * 3. AA.BB.CC
 * 4. XX.YY.ZZ
 *
 * Line 3 and 4 name the start and end of the range respectively.
 * There are two possible implementations: normal and negated.
 *
 * - Example on-state with normal implementation and configuration from above:
 *   onState <=> AA.BB.CC <= IP <= XX.YY.ZZ
 *
 * - Example on-state with negated implementation and configuration from above:
 *   onState <=> !(AA.BB.CC <= IP <= XX.YY.ZZ)
 */
abstract class AbstractBC9037 extends AbstractSimpleCrossroad implements Triggable {

    AbstractBC9037(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
    }

    /**
     *
     * @return True if the sign uses the negated result of @{@link #isAddressMatching()}.
     */
    protected abstract boolean negated();

    @Override
    protected void addIO() {
        super.addIO();
        // add input [0] to [2] from vehicle
        addIOInv();
        // add input [3], [4] and [5] from 4th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 3));

        // add input [6], [7] and [8] from 3th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 2));
    }

    /**
     * Utility method to check whether a integer is between lower bound l and upper bound u.
     */
    private boolean in(int l, int v, int u) {
        return l <= v && v <= u;
    }

    /**
     * Check if the vehicle IP is in the configured range.
     *
     * The return value depends on the return value of @{@link #negated()}.
     * The result is negated if said method returns true.
     *
     */
    private boolean isAddressMatching() {
        try {
            int startRegion = getInput(6).getAmount();
            int region = getInput(0).getAmount();
            int endRegion = getInput(3).getAmount();

            int startTrack = getInput(7).getAmount();
            int track = getInput(1).getAmount();
            int endTrack = getInput(4).getAmount();

            int startStation = getInput(8).getAmount();
            int station = getInput(2).getAmount();
            int endStation = getInput(5).getAmount();

            boolean value = in(startRegion, region, endRegion) &&
                    in(startTrack, track, endTrack) &&
                    in(startStation, station, endStation);

            if (negated()) {
                return !value;
            }
            return value;
        } catch (NullPointerException e) {
            // There is no address on sign
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.Signs.AbstractSimpleCrossroad#route()
     */
    @Override
    protected IntersectionSide.Side route() {
        if (!WandererContentFactory.isWanderer(getInventory()) && this.isAddressMatching()) {
            return IntersectionSide.Side.LEVER_ON;
        }
        return IntersectionSide.Side.LEVER_OFF;
    }

    private void addAddressAsInputs(Address addr) {
        if (addr.isValid()) {
            RegistryInput region = addr.getRegion();
            this.addInputRegistry(region);

            RegistryInput track = addr.getTrack();
            this.addInputRegistry(track);

            RegistryBoth station = addr.getStation();
            this.addInputRegistry(station);
        }
    }
}
