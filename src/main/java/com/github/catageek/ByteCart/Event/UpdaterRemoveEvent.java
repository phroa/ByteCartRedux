package com.github.catageek.ByteCart.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class UpdaterRemoveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final int vehicleId;

    /**
     * Default constructor
     *
     * @param VehicleId the vehicle id
     * @param location the location
     */
    public UpdaterRemoveEvent(int VehicleId) {
        this.vehicleId = VehicleId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return the vehicleId
     */
    public int getVehicleId() {
        return vehicleId;
    }
}
