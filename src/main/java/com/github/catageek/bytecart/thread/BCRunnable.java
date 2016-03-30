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

import com.github.catageek.bytecart.ByteCartRedux;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Map;

/**
 * A class that schedule tasks to manage the automatic removal of elements
 *
 * @param <K> key used in the map or set
 */
final class BCRunnable<K> {

    private final Expirable<K> expirable;
    private final K key;

    /**
     * @param expirable the Collection implementing expirable
     * @param key the key of the collection to which this task will be referenced
     */
    BCRunnable(Expirable<K> expirable, K key) {
        this.expirable = expirable;
        this.key = key;
    }

    /**
     * Schedule or reschedule an expirable task with a specific timeout delay
     *
     * @param duration the timeout to set
     * @param objects arguments to pass to the abstract expirable.expire() method
     * @return the BukkitTask scheduled
     */
    Task renewTaskLater(long duration, Object... objects) {
        Task task;
        Map<K, Task> map = expirable.getThreadMap();
        synchronized (map) {
            if (!expirable.getThreadMap().containsKey(key)) {
                task = (expirable.isSync() ? this.runTaskLater(objects) : this.runTaskLaterAsynchronously(objects));

            } else {
                Task old = expirable.getThreadMap().get(key);
                Runnable runnable = new Expire(expirable, key, objects);


                if (old.isAsynchronous()) {
                    task = runTaskLaterAsynchronously(runnable);
                } else {
                    task = runTaskLater(runnable);
                }
                old.cancel();
            }

            expirable.getThreadMap().put(key, task);
        }

        return task;
    }

    /**
     * Schedule or reschedule an expirable task with the default timeout delay
     *
     * @param objects arguments to pass to the abstract expirable.expire() method
     * @return the BukkitTask scheduled
     */
    Task renewTaskLater(Object... objects) {
        return renewTaskLater(expirable.getDuration(), objects);
    }

    /**
     * Cancel the task
     *
     */
    void cancel() {
        if (expirable.getThreadMap().containsKey(key)) {
            expirable.getThreadMap().remove(key);
        }
    }

    /**
     * Schedule an expirable task synchronously
     *
     * @param objects the arguments to pass to expirable.expire() method
     * @return the task scheduled
     */
    Task runTaskLater(Object... objects) {
        Runnable runnable = new Expire(expirable, key, objects);
        Task task = Sponge.getScheduler().createTaskBuilder().delayTicks(expirable.getDuration()).execute(runnable).submit(ByteCartRedux.myPlugin);
        return task;
    }

    /**
     * Schedule an expirable task asynchronously
     *
     * @param objects the arguments to pass to expirable.expire() method
     * @return the task scheduled
     */
    Task runTaskLaterAsynchronously(Object... objects) {
        Runnable runnable = new Expire(expirable, key, objects);
        Task task = Sponge.getScheduler().createTaskBuilder().async().delayTicks(expirable.getDuration()).execute(runnable)
                .submit(ByteCartRedux.myPlugin);
        return task;
    }

    /**
     * A runnable that will execute the expire() method and clean internal map
     */
    private final class Expire implements Runnable {

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
            Map<K, Task> map = this.expirable.getThreadMap();

            map.remove(key);
            this.expirable.expire(params);
        }

    }

}

