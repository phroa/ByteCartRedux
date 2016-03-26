package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a region reset updater clears the address
 * of a BC9XXX sign (except BC9001).
 */
public class UpdaterClearSubnetEvent extends UpdaterClearStationEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int length;

    /**
     * Default constructor
     *
     * @param updater The updater involved
     * @param oldAddress The old address of the subnet
     * @param length number of stations this subnet can contain
     */
    public UpdaterClearSubnetEvent(Wanderer updater, Address oldAddress, int length) {
        super(updater, oldAddress, "");
        this.length = length;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the numbers of address this subnet can contain
     *
     * @return The number of address
     */
    public int getLength() {
        return length;
    }
}
