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
package com.github.catageek.ByteCart.Routing;

import com.github.catageek.ByteCart.Wanderer.RouteValue;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * A track number on 11 bits (from 0 to 2047)
 */
final class RouteNumber extends RoutingTableContent<RouteNumber>
        implements Comparable<RouteNumber>, Externalizable, RouteValue {


    private static final int rlength = 11;
    /**
     *
     */
    private static final long serialVersionUID = -8112012047943458459L;

    public RouteNumber() {
        super(rlength);
    }

    RouteNumber(int route) {
        super(route, rlength);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeShort(this.value());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setValue(in.readShort() & ((1 << rlength) - 1));
    }
}
