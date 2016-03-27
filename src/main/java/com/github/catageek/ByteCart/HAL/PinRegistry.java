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
package com.github.catageek.ByteCart.HAL;

import com.github.catageek.ByteCart.IO.InputPin;
import com.github.catageek.ByteCart.IO.OutputPin;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * A registry implementation
 *
 * @param <T> InputPin or OutputPin type
 */
public class PinRegistry<T> implements RegistryInput, RegistryOutput, Registry {

    final protected List<T> PinArray;

    /**
     * @param pins an array of pins
     */
    public PinRegistry(T[] pins) {
        this.PinArray = Arrays.asList(pins);
/*		if(ByteCartRedux.debug)
            ByteCartRedux.log.info("ByteCartRedux : creating PinRegistry with" + this.length() + "pin(s)");
*/
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.Registry#length()
     */
    @Override
    public int length() {
        return PinArray.size();
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.Registry#getAmount()
     */
    @Override
    public int getAmount() {

        int amount = 0;
        int i = 1;

        for (ListIterator<T> it = this.PinArray.listIterator(this.length()); it.hasPrevious(); i = i << 1) {
            if (it.previous() != null) {

                it.next();

                if (((InputPin) it.previous()).read()) {
                    amount += i;

                }

            }
        }
        return amount;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.RegistryOutput#setAmount(int)
     */
    @Override
    public void setAmount(int amount) {
        int i = amount;


        for (ListIterator<T> it = this.PinArray.listIterator(this.length()); it.hasPrevious(); i = i >> 1) {
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

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.RegistryOutput#setBit(int, boolean)
     */
    @Override
    public void setBit(int index, boolean value) {
        ((OutputPin) this.PinArray.get(index)).write(value);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.RegistryInput#getBit(int)
     */
    @Override
    public boolean getBit(int index) {
        return ((InputPin) this.PinArray.get(index)).read();
    }


}
