package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a region reset updater clears the address
 * of a BC9001 sign.
 */
public class UpdaterClearStationEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address oldAddress;
    private final String name;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the station
     * @param name The name of the station
     */
    public UpdaterClearStationEvent(Wanderer updater, Address oldAddress, String name) {
        super(updater);
        this.oldAddress = oldAddress;
        this.name = name;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the old address erased
     *
     * @return A String containing the address
     */
    public String getOldAddress() {
        return oldAddress.toString();
    }

    /**
     * Get the name of the station
     *
     * @return The name
     */
    public String getName() {
        return name;
    }
}
