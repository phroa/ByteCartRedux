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
package com.github.catageek.bytecart.thread;

import org.spongepowered.api.scheduler.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * HashMap that keeps entries during "duration" ticks
 */

public abstract class Expirable<K> {

    private final long duration;
    private final String name;
    private final boolean isSync;
    private Map<K, Task> threadMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * @param duration the timeout value
     * @param isSync true if the element must be removed synchronously in the main thread
     * @param name a name for the set
     */
    public Expirable(long duration, boolean isSync, String name) {
        super();
        this.duration = duration;
        this.name = name;
        this.isSync = isSync;
    }

    abstract public void expire(Object... objects);

    public void reset(K key, Object... objects) {
        if (duration != 0) {
            (new BCRunnable<K>(this, key)).renewTaskLater(objects);
        }
    }

    public void reset(long duration, K key, Object... objects) {
        if (duration != 0) {
            (new BCRunnable<K>(this, key)).renewTaskLater(duration, objects);
        }
    }

    public final void cancel(K key) {
        if (duration != 0) {
            (new BCRunnable<K>(this, key)).cancel();
        }
    }

    protected final Map<K, Task> getThreadMap() {
        return threadMap;
    }

    public final long getDuration() {
        return duration;
    }

    public final boolean isSync() {
        return isSync;
    }

    public final String getName() {
        return name;
    }


}
