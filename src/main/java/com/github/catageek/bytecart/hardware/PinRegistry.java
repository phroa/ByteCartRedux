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
package com.github.catageek.bytecart.hardware;

import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.OutputPin;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * A registry implementation
 *
 * @param <T> InputPin or OutputPin type
 */
public class PinRegistry<T> implements RegistryBoth {

    private final List<T> pins;

    /**
     * @param pins an array of pins
     */
    public PinRegistry(T[] pins) {
        this.pins = Arrays.asList(pins);
    }

    @Override
    public int length() {
        return pins.size();
    }

    @Override
    public int getValue() {

        int amount = 0;
        int i = 1;

        for (ListIterator<T> it = this.pins.listIterator(this.length()); it.hasPrevious(); i = i << 1) {
            if (it.previous() != null) {

                it.next();

                if (((InputPin) it.previous()).read()) {
                    amount += i;

                }

            }
        }
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        int i = amount;


        for (ListIterator<T> it = this.pins.listIterator(this.length()); it.hasPrevious(); i = i >> 1) {
            if (it.previous() != null) {

                it.next();

                if ((i & 1) != 0) {
                    ((OutputPin) it.previous()).write(true);

                } else {
                    ((OutputPin) it.previous()).write(false);

                }
            }
        }


    }

    @Override
    public void setBit(int index, boolean value) {
        ((OutputPin) this.pins.get(index)).write(value);
    }

    @Override
    public boolean getBit(int index) {
        return ((InputPin) this.pins.get(index)).read();
    }


}
