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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A treemap that can be saved (i.e externalized) as a binary stream
 *
 * @param <K> the key type of the map
 * @param <V> the value type of the map
 */
public final class ExternalizableTreeMap<K extends Externalizable, V extends Externalizable>
        extends TreeMap<K, V> implements Externalizable {

    private static final long serialVersionUID = 1074583778619610579L;

    public ExternalizableTreeMap() {
        super();
    }

    public ExternalizableTreeMap(Comparator<? super K> comparator) {
        super(comparator);
    }

    public ExternalizableTreeMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public ExternalizableTreeMap(SortedMap<K, ? extends V> arg0) {
        super(arg0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput s) throws IOException,
            ClassNotFoundException {
        // Read in size
        int size = s.readShort();

        for (int i = 0; i < size; i++) {
            K key = (K) s.readObject();
            V value = (V) s.readObject();
            this.put(key, value);
        }

    }

    @Override
    public void writeExternal(ObjectOutput s) throws IOException {
        // Write out size (number of Mappings)
        s.writeShort(size());

        // Write out keys and values (alternating)
        for (Map.Entry<K, V> e : entrySet()) {
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }
}
