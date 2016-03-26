package com.github.catageek.ByteCart.Wanderer;

import java.io.IOException;

import org.bukkit.inventory.Inventory;

import com.github.catageek.ByteCart.Signs.BCSign;

public interface WandererFactory {
	
	/**
	 * @return a new wanderer instance
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	Wanderer getWanderer(BCSign bc, Inventory inv) throws ClassNotFoundException, IOException;

}
