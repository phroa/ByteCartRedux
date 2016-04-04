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
package com.github.catageek.bytecart.event;

import com.github.catageek.bytecart.event.custom.UpdaterRemoveEvent;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

class ByteCartUpdaterDestructEvent implements EventListener<DestructEntityEvent> {

    /**
     * Detect a destroyed updater
     *
     * @param event
     */
    @Listener
    @Override
    public void handle(DestructEntityEvent event) throws Exception {
        Entity v = event.getTargetEntity();
        if (v instanceof Carrier) {
            CarriedInventory inv = ((Carrier) v).getInventory();
            if (WandererContentFactory.isWanderer(inv, "Updater")) {
                Sponge.getEventManager().post(new UpdaterRemoveEvent(v.getUniqueId()));
            }
        }
        Sponge.getEventManager().unregisterListeners(this);
    }
}
