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
/**
 *
 */
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.AddressLayer.AddressFactory;
import com.github.catageek.ByteCart.AddressLayer.AddressString;
import com.github.catageek.ByteCart.HAL.RegistryBoth;
import com.github.catageek.ByteCart.HAL.RegistryInput;

import java.util.Random;

/**
 * Gives random address to a cart
 */
final class BC7019 extends BC7010 implements Triggable {

    BC7019(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        this.StorageCartAllowed = true;
    }

    @Override
    public String getName() {
        return "BC7019";
    }

    @Override
    public String getFriendlyName() {
        return "Random address";
    }

    @Override
    protected Address getAddressToWrite() {
        int startRegion = getInput(3).getAmount();
        int endRegion = getInput(0).getAmount();

        int newRegion = startRegion + (new Random()).nextInt(endRegion - startRegion + 1);

        int startTrack = getInput(4).getAmount();
        int endTrack = getInput(1).getAmount();

        int newTrack = startTrack + (new Random()).nextInt(endTrack - startTrack + 1);

        int startStation = getInput(5).getAmount();
        int endStation = getInput(2).getAmount();

        int newStation = startStation + (new Random()).nextInt(endStation - startStation + 1);

        StringBuilder sb = new StringBuilder();
        String dot = ".";

        sb.append(newRegion).append(dot).append(newTrack).append(dot).append(newStation);

        return new AddressString(sb.toString(), false);
    }


    protected void addIO() {
        // add input [0], [1] and [2] from 4th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 3));

        // add input [3], [4] and [5] from 3th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 2));
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
