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
import java.util.HashMap;
import java.util.Map;

/**
 * A map in which elements are deleted after a timeout
 *
 * @param <K> the type of keys of the set
 * @param <T> the type of values of the set
 */
public final class ExpirableMap<K, T> extends Expirable<K> {

    private final Map<K, T> Map = Collections.synchronizedMap(new HashMap<K, T>());

    public ExpirableMap(long duration, boolean isSync, String name) {
        super(duration, isSync, name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void expire(Object... objects) {
        ((Map<K, ?>) objects[1]).remove(objects[0]);
    }

    /**
     * Add an element to the map
     *
     * @param key key of the element
     * @param value value of the element
     * @param reset must be set to true to reset the timeout to initial value, false otherwise
     * @return true if the element was added
     */
    public boolean put(K key, T value, boolean reset) {
        if (reset) {
            this.reset(key, key, Map);
        }
        return (Map.put(key, value) == null);
    }

    /**
     * Add an element to the map
     *
     * @param key key of the element
     * @param value value of the element
     * @return true if the element was added
     */
    public boolean put(K key, T value) {
        return this.put(key, value, true);
    }

    @Override
    public void reset(K key, Object... objects) {
        super.reset(key, key, Map);
    }

    /**
     * Remove an element from the map
     *
     * @param key the key of the element
     */
    public final void remove(K key) {
        Map.remove(key);
        this.cancel(key);
    }


    /**
     * Get the value of the element having a specific key
     *
     * @param key the key of the element
     * @return the value
     */
    public T get(K key) {
        return Map.get(key);
    }

    /**
     * Tell if the map contains an element
     *
     * @param key the element to check
     * @return true if the element is in the map
     */
    public boolean contains(K key) {
        return Map.containsKey(key);
    }

    /**
     * Remove all the elements of the map
     *
     */
    public void clear() {
        Map.clear();
    }

    /**
     * Tell if the map is empty
     *
     * @return true if the map is empty
     */
    public boolean isEmpty() {
        return Map.isEmpty();
    }
}
