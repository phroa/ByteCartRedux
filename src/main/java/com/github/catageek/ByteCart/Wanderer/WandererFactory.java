package com.github.catageek.ByteCart.Wanderer;

import com.github.catageek.ByteCart.Signs.BCSign;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface WandererFactory {

    /**
     * @return a new wanderer instance
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Wanderer getWanderer(BCSign bc, Inventory inv) throws ClassNotFoundException, IOException;

}
