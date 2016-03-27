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

import com.github.catageek.bytecart.collection.PartitionedHashSet;
import com.github.catageek.bytecart.util.DirectionRegistry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * The content of a routing table entry
 */
final class RouteProperty implements Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = -3548458365177323172L;

    private TreeMap<Metric, PartitionedHashSet<DirectionRegistry>> map = new TreeMap<Metric, PartitionedHashSet<DirectionRegistry>>();

    public RouteProperty() {
    }

    /**
     * @return the map associating each metric with a DirectionRegistry
     */
    final TreeMap<Metric, PartitionedHashSet<DirectionRegistry>> getMap() {
        return map;
    }

    /**
     * Decompose the registry value to a set of DirectionRegistry
     *
     * @param value the value to decompose
     * @return the set
     */
    public final PartitionedHashSet<DirectionRegistry> getPartitionedHashSet(int value) {
        int reg = value;
        int cur = 1;
        PartitionedHashSet<DirectionRegistry> set = new PartitionedHashSet<DirectionRegistry>();
        while (reg != 0) {
            if ((reg & 1) != 0) {
                set.add(new DirectionRegistry(cur));
            }
            cur = cur << 1;
            reg = reg >> 1;
        }
        return set;
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException,
            ClassNotFoundException {
        int size = arg0.readInt();
        for (int i = 0; i < size; i++) {
            int value = arg0.readUnsignedShort();
            PartitionedHashSet<DirectionRegistry> set;
            if (!(set = getPartitionedHashSet(value & 15)).isEmpty()) {
                map.put(new Metric(value >> 4), set);
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeInt(map.size());
        Iterator<Entry<Metric, PartitionedHashSet<DirectionRegistry>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Metric, PartitionedHashSet<DirectionRegistry>> entry = it.next();
            arg0.writeShort((entry.getKey().value() << 4) + entry.getValue().getPartitionedValue());
        }
    }

}
