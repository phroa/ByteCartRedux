package com.github.catageek.ByteCart;

import com.github.catageek.ByteCart.AddressLayer.Resolver;
import com.github.catageek.ByteCart.Wanderer.WandererManager;

import java.util.logging.Logger;


public interface ByteCartPlugin {

    /**
     * @return the resolver registered
     */
    public Resolver getResolver();

    /**
     * Set the resolver that will be used
     *
     * @param resolver the resolver provided
     */
    public void setResolver(Resolver resolver);

    /**
     * Get the logger
     *
     * @return the logger
     */
    public Logger getLog();

    /**
     * @return the wanderer factory
     */
    public WandererManager getWandererManager();
}
