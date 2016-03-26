package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.HAL.IC;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a sign is physically removed from the world
 */
public final class SignRemoveEvent extends BCEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;

    public SignRemoveEvent(IC ic, Entity entity) {
        super(ic);
        this.entity = entity;
    }

    /**
     * Needed for Bukkit Event API usage
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Event#getHandlers()
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the entity that broke the block
     */
    public Entity getEntity() {
        return entity;
    }

}
