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

import com.github.catageek.ByteCart.HAL.RegistryBoth;
import com.github.catageek.ByteCart.HAL.VirtualRegistry;
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
    protected void setValue(int amount) {
        data.setAmount(amount);
    }

    /**
     * @return the value stored
     */
    public final int value() {
        return data.getAmount();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(T o) {
        return new CompareToBuilder().append(value(), o.value()).toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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