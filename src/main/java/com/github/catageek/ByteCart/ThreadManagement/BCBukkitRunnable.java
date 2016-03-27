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
package com.github.catageek.ByteCart.ThreadManagement;

import com.github.catageek.ByteCart.ByteCartRedux;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

/**
 * A class that schedule tasks to manage the automatic removal of elements
 *
 * @param <K> key used in the map or set
 */
final class BCBukkitRunnable<K> {

    private final Expirable<K> Expirable;
    private final K Key;

    /**
     * @param expirable the Collection implementing Expirable
     * @param key the key of the collection to which this task will be referenced
     */
    BCBukkitRunnable(Expirable<K> expirable, K key) {
        this.Expirable = expirable;
        this.Key = key;
    }

    /**
     * Schedule or reschedule an Expirable task with a specific timeout delay
     *
     * @param duration the timeout to set
     * @param objects arguments to pass to the abstract Expirable.expire() method
     * @return the BukkitTask scheduled
     */
    BukkitTask renewTaskLater(long duration, Object... objects) {
        BukkitTask task;
        Map<K, BukkitTask> map = Expirable.getThreadMap();
        synchronized (map) {
            if (!Expirable.getThreadMap().containsKey(Key)) {
                task = (Expirable.isSync() ? this.runTaskLater(objects)
                        : this.runTaskLaterAsynchronously(objects));

            } else {
                BukkitTask old = Expirable.getThreadMap().get(Key);
                BukkitRunnable runnable = new Expire(Expirable, Key, objects);


                if (old.isSync()) {
                    task = runnable.runTaskLater(ByteCartRedux.myPlugin, duration);
                } else {
                    task = runnable.runTaskLaterAsynchronously(ByteCartRedux.myPlugin, duration);
                }
                old.cancel();
            }

            Expirable.getThreadMap().put(Key, task);
        }

        return task;
    }

    /**
     * Schedule or reschedule an Expirable task with the default timeout delay
     *
     * @param objects arguments to pass to the abstract Expirable.expire() method
     * @return the BukkitTask scheduled
     */
    BukkitTask renewTaskLater(Object... objects) {
        return renewTaskLater(Expirable.getDuration(), objects);
    }

    /**
     * Cancel the task
     *
     */
    void cancel() {
        if (Expirable.getThreadMap().containsKey(Key)) {
            Expirable.getThreadMap().remove(Key);
        }
    }

    /**
     * Schedule an Expirable task synchronously
     *
     * @param objects the arguments to pass to Expirable.expire() method
     * @return the task scheduled
     */
    BukkitTask runTaskLater(Object... objects) {
        BukkitRunnable runnable = new Expire(Expirable, Key, objects);
        org.bukkit.scheduler.BukkitTask task = runnable.runTaskLater(ByteCartRedux.myPlugin, Expirable.getDuration());
        return task;
    }

    /**
     * Schedule an Expirable task asynchronously
     *
     * @param objects the arguments to pass to Expirable.expire() method
     * @return the task scheduled
     */
    BukkitTask runTaskLaterAsynchronously(Object... objects) {
        BukkitRunnable runnable = new Expire(Expirable, Key, objects);
        org.bukkit.scheduler.BukkitTask task = runnable.runTaskLaterAsynchronously(ByteCartRedux.myPlugin, Expirable.getDuration());
        return task;
    }

    /**
     * A runnable that will execute the expire() method and clean internal map
     */
    private final class Expire extends BukkitRunnable {

        private final Expirable<K> expirable;
        private final K key;
        private final Object[] params;

        public Expire(Expirable<K> expirable, K key, Object... objects) {
            this.key = key;
            this.expirable = expirable;
            this.params = objects;
        }

        @Override
        public void run() {
            Map<K, BukkitTask> map = this.expirable.getThreadMap();

            map.remove(key);
            this.expirable.expire(params);
        }

    }

}

