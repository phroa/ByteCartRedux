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
