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
package com.github.catageek.bytecart.updater;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collection.ExpirableSet;
import com.github.catageek.bytecart.event.custom.UpdaterRemoveEvent;
import org.spongepowered.api.Sponge;

import java.util.Iterator;
import java.util.UUID;

/**
 * A set for integers with a timeout of 1h
 */
public final class UpdaterSet {

    private final ExpirableSet<UUID> updateSet;

    // entries stay for 1h
    public UpdaterSet() {
        long duration = ByteCartRedux.rootNode.getNode("updater", "timeout").getInt(60) * 1200;
        updateSet = new ExpirableSet<>(duration, false, "UpdaterRoutes");
    }

    public ExpirableSet<UUID> getMap() {
        return updateSet;
    }

    public boolean isUpdater(UUID id) {
        return updateSet.contains(id);
    }

    public void addUpdater(UUID id) {
        this.updateSet.add(id);
    }

    public void clear() {
        Iterator<UUID> it = updateSet.getIterator();
        while (it.hasNext()) {
            Sponge.getEventManager().post(new UpdaterRemoveEvent(it.next()));
        }
        updateSet.clear();
    }
}
