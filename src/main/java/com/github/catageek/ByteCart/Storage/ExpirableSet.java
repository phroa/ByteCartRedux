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
package com.github.catageek.ByteCart.Storage;

import com.github.catageek.ByteCart.ThreadManagement.Expirable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A set in which elements are deleted after a timeout
 *
 * @param <K> the type of elements of the set
 */
public final class ExpirableSet<K> extends Expirable<K> {

    private final Set<K> Set = Collections.synchronizedSet(new HashSet<K>());

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.ThreadManagement.Expirable#Expirable(long, boolean, String)
     */
    public ExpirableSet(long duration, boolean isSync, String name) {
        super(duration, isSync, name);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.ThreadManagement.Expirable#expire(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public void expire(Object... objects) {
        ((Set<K>) objects[1]).remove(objects[0]);
    }

    /**
     * Add an element to the set
     *
     * @param key the element to add
     * @return true if the element was added
     */
    public boolean add(K key) {
        //		if(ByteCartRedux.debug)
        //			ByteCartRedux.log.info("ByteCartRedux: create ephemeral key (" + key +") in " + this.getName() + " for " + this.getDuration() + " ticks");
        this.reset(key, key, Set);
        return Set.add(key);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.ThreadManagement.Expirable#reset(java.lang.Object, java.lang.Object[])
     */
    @Override
    public void reset(K key, Object... objects) {
        super.reset(key, key, Set);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.ThreadManagement.Expirable#reset(long, java.lang.Object, java.lang.Object[])
     */
    @Override
    public void reset(long duration, K key, Object... objects) {
        super.reset(duration, key, key, Set);
    }

    /**
     * Remove the element from the set
     *
     * @param key the element to remove
     */
    public final void remove(K key) {
        Set.remove(key);
        this.cancel(key);
    }


    /**
     * Tells if the set contains an element
     *
     * @param key the element to check
     * @return true if the element is in the set
     */
    public boolean contains(K key) {
        return Set.contains(key);
    }

    /**
     * Tells if the set is empty
     *
     * @return true if the set is empty
     */
    public boolean isEmpty() {
        return Set.isEmpty();
    }

    /**
     * Empty the set
     */
    public void clear() {
        Iterator<K> it = Set.iterator();
        while (it.hasNext()) {
            this.cancel(it.next());
        }
        Set.clear();
    }

    public Iterator<K> getIterator() {
        return Set.iterator();
    }
}