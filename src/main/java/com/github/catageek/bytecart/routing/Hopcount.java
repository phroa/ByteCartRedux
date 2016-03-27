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
package com.github.catageek.bytecart.routing;

import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.hardware.VirtualRegistry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


final class Hopcount implements Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = -8527436068446548316L;
    private final RegistryBoth value = new VirtualRegistry(8);

    public Hopcount() {
    }

    Hopcount(int amount) {
        this.value.setAmount(amount);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(this.value.getAmount());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value.setAmount(in.readUnsignedByte());
    }

    final int getValue() {
        return value.getAmount();
    }
}
