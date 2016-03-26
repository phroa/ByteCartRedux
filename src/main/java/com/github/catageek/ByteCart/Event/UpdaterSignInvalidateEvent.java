package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a sign is invalidated by an updater
 */
public final class UpdaterSignInvalidateEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();

    public UpdaterSignInvalidateEvent(Wanderer updater) {
        super(updater);
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
}