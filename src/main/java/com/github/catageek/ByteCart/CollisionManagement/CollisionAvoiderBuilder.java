package com.github.catageek.ByteCart.CollisionManagement;

import com.github.catageek.ByteCart.Signs.Triggable;
import org.bukkit.Location;

/**
 * A builder for a collision manager
 */
public interface CollisionAvoiderBuilder {

    /**
     * Get an instance of the collision manager
     *
     * @return an instance of collision manager
     */
    public <T extends CollisionAvoider> T getCollisionAvoider();

    /**
     * Get the location to where the collision managers built will be attached
     *
     *
     * @return the location
     */
    public Location getLocation();

    /**
     * Get the IC attached to the collision managers built
     *
     *
     * @return the IC
     */
    public Triggable getIc();

}
