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
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * A raw routing table entry, i.e a registry
 *
 * @param <T> the type of content that will be stored
 */
abstract class RoutingTableContent<T extends RoutingTableContent<T>> implements Comparable<T> {

    /**
     *
     */
    private final int length;
    private final RegistryBoth data;

    RoutingTableContent(int length) {
        this.length = length;
        data = new VirtualRegistry(length);
    }

    RoutingTableContent(int amount, int length) {
        this(length);
        this.data.setAmount(amount);
    }

    /**
     * @return the size of the registry in bits
     */
    final int size() {
        return length;
    }

    /**
     * @param amount the value to store
     */
    void setValue(int amount) {
        data.setAmount(amount);
    }

    /**
     * @return the value stored
     */
    public final int value() {
        return data.getValue();
    }

    @Override
    public int compareTo(T o) {
        return new CompareToBuilder().append(value(), o.value()).toComparison();
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
        if (!(o instanceof RoutingTableContent<?>)) {
            return false;
        }

        RoutingTableContent<?> rhs = (RoutingTableContent<?>) o;

        return new EqualsBuilder().append(value(), rhs.value()).isEquals();
    }
}