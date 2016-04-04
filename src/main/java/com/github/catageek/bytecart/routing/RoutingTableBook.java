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

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collection.ExternalizableTreeMap;
import com.github.catageek.bytecart.collection.PartitionedHashSet;
import com.github.catageek.bytecart.file.BookFile;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * A routing table in a book
 */
final class RoutingTableBook extends AbstractRoutingTable implements
        RoutingTableWritable, Externalizable {

    private static final long serialVersionUID = -7013741680310224056L;
    private boolean wasModified = false;
    private ExternalizableTreeMap<RouteNumber, RouteProperty> map = new ExternalizableTreeMap<RouteNumber, RouteProperty>();
    private CarriedInventory<?> inventory;

    public RoutingTableBook() {
    }

    RoutingTableBook(CarriedInventory<?> inv) {
        this.inventory = inv;
    }

    /**
     * Set the inventory
     *
     * @param inventory the inventory
     */
    final void setInventory(CarriedInventory<?> inventory) {
        this.inventory = inventory;
    }

    @Override
    public void clear(boolean fullreset) {
        if (map.isEmpty()) {
            return;
        }

        RouteProperty routes = null;
        RouteNumber route = new RouteNumber(0);

        if (!fullreset && map.containsKey(route)) {
            routes = map.get(route);
        }

        map.clear();

        if (!fullreset && routes != null) {
            map.put(route, routes);
        }

        if (ByteCartRedux.debug) {
            ByteCartRedux.log.info("ByteCartRedux : clear routing table map");
        }
        wasModified = true;
    }

    @Override
    public final <T extends RouteValue> Iterator<T> getOrderedRouteNumbers() {
        @SuppressWarnings("unchecked")
        Iterator<T> it = ((SortedSet<T>) map.keySet()).iterator();
        return it;
    }

    @Override
    public int getMetric(int entry, DirectionRegistry direction) {
        SortedMap<Metric, PartitionedHashSet<DirectionRegistry>> smap;
        RouteNumber route = new RouteNumber(entry);
        if (map.containsKey(route) && (smap = map.get(route).getMap()) != null
                && !smap.isEmpty()) {
            Iterator<Metric> it = smap.keySet().iterator();
            Metric d;
            while (it.hasNext()) {
                if (smap.get((d = it.next())).contains(direction)) {
                    return d.value();
                }
            }
        }
        return -1;
    }

    @Override
    public int getMinMetric(int entry) {
        SortedMap<Metric, PartitionedHashSet<DirectionRegistry>> smap;
        RouteNumber route = new RouteNumber(entry);
        if (map.containsKey(route) && (smap = map.get(route).getMap()) != null
                && !smap.isEmpty()) {
            return smap.firstKey().value();
        }
        return -1;
    }

    private void setMapEntry(int entry, DirectionRegistry direction, Metric metric) {

        RouteNumber route = new RouteNumber(entry);
        Metric dist = new Metric(metric);
        RouteProperty smap;
        PartitionedHashSet<DirectionRegistry> set;

        if ((smap = map.get(route)) == null) {
            smap = new RouteProperty();
            map.put(route, smap);
            wasModified = true;
        }

        if ((set = smap.getMap().get(dist)) == null) {
            set = new PartitionedHashSet<DirectionRegistry>(3);
            smap.getMap().put(dist, set);
            wasModified = true;
        }
        wasModified |= set.add(direction);
    }


    @Override
    public void setEntry(int entry, DirectionRegistry direction, Metric metric) {
        setMapEntry(entry, direction, metric);
    }

    @Override
    public boolean isEmpty(int entry) {
        return !map.containsKey(new RouteNumber(entry));
    }

    @Override
    public DirectionRegistry getDirection(int entry) {
        RouteNumber route = new RouteNumber(entry);
        Set<DirectionRegistry> set;
        TreeMap<Metric, PartitionedHashSet<DirectionRegistry>> pmap;
        if (map.containsKey(route) && (pmap = map.get(route).getMap()) != null && !pmap.isEmpty()) {
            set = pmap.firstEntry().getValue();
            if (!set.isEmpty()) {
                return set.toArray(new DirectionRegistry[set.size()])[0];
            }
            throw new AssertionError("Set<DirectionRegistry> in RoutingTableWritable is empty.");
        }
        return null;
    }

    @Override
    public Set<Integer> getDirectlyConnectedList(DirectionRegistry direction) {
        SortedMap<Integer, Metric> list = new TreeMap<Integer, Metric>();
        Iterator<Entry<RouteNumber, RouteProperty>> it = map.entrySet().iterator();
        Entry<RouteNumber, RouteProperty> entry;
        Metric zero = new Metric(0);
        TreeMap<Metric, PartitionedHashSet<DirectionRegistry>> smap;
        PartitionedHashSet<DirectionRegistry> set;

        while (it.hasNext()) {
            entry = it.next();
            if ((smap = entry.getValue().getMap()) != null && smap.containsKey(zero)
                    && !(set = smap.get(zero)).isEmpty()
                    && set.contains(direction)) {
                // just extract the connected route
                list.put(entry.getKey().value(), zero);
            }
        }
        return list.keySet();
    }

    @Override
    protected Set<Integer> getNotDirectlyConnectedList(
            DirectionRegistry direction) {
        SortedMap<Integer, Metric> list = new TreeMap<Integer, Metric>();
        Iterator<Entry<RouteNumber, RouteProperty>> it = map.entrySet().iterator();
        Entry<RouteNumber, RouteProperty> entry;
        Metric zero = new Metric(0);
        Metric one = new Metric(1);
        SortedMap<Metric, PartitionedHashSet<DirectionRegistry>> smap;
        PartitionedHashSet<DirectionRegistry> set;

        while (it.hasNext()) {
            entry = it.next();

            if ((smap = entry.getValue().getMap()) == null
                    || !(smap = entry.getValue().getMap()).containsKey(zero)
                    || !(!(set = smap.get(zero)).isEmpty()
                    && set.contains(direction))) {
                // extract routes going to directions with distance > 0
                smap = smap.tailMap(one);
                Iterator<Metric> it2 = smap.keySet().iterator();
                while (it2.hasNext()) {
                    Metric d = it2.next();
                    if (smap.get(d).contains(direction)) {
                        list.put(entry.getKey().value(), d);
                        break;
                    }
                }
            }
        }
        return list.keySet();
    }

    @Override
    public void removeEntry(int entry, DirectionRegistry from) {
        RouteNumber route = new RouteNumber(entry);
        TreeMap<Metric, PartitionedHashSet<DirectionRegistry>> smap;
        Set<DirectionRegistry> set;

        if (map.containsKey(route) && (smap = map.get(route).getMap()) != null) {
            Iterator<Metric> it = smap.keySet().iterator();
            while (it.hasNext()) {
                wasModified |= (set = smap.get(it.next())).remove(from);
                if (set.isEmpty()) {
                    it.remove();
                }
                if (smap.isEmpty()) {
                    map.remove(route);
                }
            }
        }
    }

    @Override
    public void serialize() throws IOException {
        if (!wasModified) {
            return;
        }
        try (BookFile file = new BookFile(inventory, 0, true)) {
            file.clear();
            ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream());
            oos.writeObject(this);
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : serialize() : object written, now closing");
            }
            oos.flush();
            wasModified = false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.map = (ExternalizableTreeMap<RouteNumber, RouteProperty>) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(map);
    }

    @Override
    public int size() {
        return map.size();
    }
}
