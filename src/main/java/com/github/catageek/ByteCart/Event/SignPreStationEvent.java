package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.CollisionManagement.IntersectionSide.Side;
import com.github.catageek.ByteCart.Signs.Subnet;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a vehicle is entering a station sign.
 *
 * This event is triggered before the "busy line" check, so the
 * direction may change.
 */
public class SignPreStationEvent extends SignPreSubnetEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Default constructor
     *
     * The side parameter may be:
     * - LEFT: the vehicle wish not to enter the station
     * - RIGHT: the vehicle wish to enter the station
     *
     * @param subnet The BC9XXX sign involved
     * @param side The direction taken by the cart
     */
    public SignPreStationEvent(Subnet subnet, Side side) {
        super(subnet, side);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
