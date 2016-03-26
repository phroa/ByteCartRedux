package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when an local updater pass a station sign.
 */

public class UpdaterPassStationEvent extends UpdaterEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Address address;
    private final String name;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param address The address of the station
     * @param name The name of the station
     */
    public UpdaterPassStationEvent(Wanderer updater, Address address, String name) {
        super(updater);
        this.address = address;
        this.name = name;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The address of the station
     */
    public Address getAddress() {
        return address;
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
