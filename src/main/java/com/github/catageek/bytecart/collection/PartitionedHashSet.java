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
package com.github.catageek.bytecart.collection;

import java.util.Collection;
import java.util.HashSet;

/**
 * A set containing powers of 2 of an integer
 *
 * @param <E> A partitionable type
 */
public class PartitionedHashSet<E extends Partitionable> extends HashSet<E> {

    /**
     *
     */
    private static final long serialVersionUID = 7798172721619367114L;

    public PartitionedHashSet() {
    }

    public PartitionedHashSet(Collection<? extends E> c) {
        super(c);
    }

    public PartitionedHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public PartitionedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Get the addition of values from the set
     *
     * @return the value
     */
    public final int getPartitionedValue() {
        int ret = 0;
        for (E e : this) {
            ret |= e.getAmount();
        }
        return ret;
    }
}
