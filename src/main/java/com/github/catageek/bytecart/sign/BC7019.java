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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressString;
import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.hardware.RegistryInput;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

import java.util.Random;

/**
 * Gives random address to a cart
 */
final class BC7019 extends BC7010 implements Triggerable {

    BC7019(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        this.storageCartAllowed = true;
    }

    @Override
    public String getName() {
        return "BC7019";
    }

    @Override
    public String getFriendlyName() {
        return "Random Destination";
    }

    @Override
    protected Address getAddressToWrite() {
        int startRegion = getInput(3).getValue();
        int endRegion = getInput(0).getValue();

        int newRegion = startRegion + (new Random()).nextInt(endRegion - startRegion + 1);

        int startTrack = getInput(4).getValue();
        int endTrack = getInput(1).getValue();

        int newTrack = startTrack + (new Random()).nextInt(endTrack - startTrack + 1);

        int startStation = getInput(5).getValue();
        int endStation = getInput(2).getValue();

        int newStation = startStation + (new Random()).nextInt(endStation - startStation + 1);

        return new AddressString(String.format("%d.%d.%d", newRegion, newTrack, newStation), false);
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
