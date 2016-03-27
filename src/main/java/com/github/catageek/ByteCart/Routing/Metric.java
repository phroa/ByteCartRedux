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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The metric component of a routing table entry
 */
public final class Metric implements Comparable<Metric>, Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = 7625856925617432369L;
    private Delay delay;

    public Metric() {
    }

    Metric(Metric m) {
        this(m.delay.getValue());
    }

    public Metric(int delay) {
        this.delay = new Delay(delay);
    }

    @Override
    public int compareTo(Metric o) {
        return new CompareToBuilder().append(value(), o.value()).toComparison();
    }

    /**
     * @return the value
     */
    public int value() {
        return delay.getValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        delay = (Delay) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(delay);
    }

    @Override
    public int hashCode() {
        return value();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Metric)) {
            return false;
        }

        Metric rhs = (Metric) o;

        return new EqualsBuilder().append(value(), rhs.value()).isEquals();
    }
}
