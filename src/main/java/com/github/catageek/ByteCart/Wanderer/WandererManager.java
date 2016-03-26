package com.github.catageek.ByteCart.Wanderer;

import java.io.IOException;

import org.bukkit.inventory.Inventory;

public interface WandererManager {
	/**
	 * Register a wanderer factory
	 * 
	 * @param wanderer the wanderer class implementing the wanderer
	 * @param name the name that will reference this type of wanderer
	 */
	public boolean register(WandererFactory wanderer, String name);
	
	/**
	 * Unregister a wanderer factory. All wanderers in the network
	 * that were created with this factory will be treated as normal carts.
	 * 
	 * @param name the name of the type of wanderer
	 */
	public void unregister(String name);
	
	/**
	 * Create a wanderer
	 * 
	 * @param ivc the content of the wanderer
	 * @param name the name of the type of wanderer previously registered
	 * @param type a suffix to add to book title
	 */
	public boolean create(InventoryContent ivc, String name, String type);

	/**
	 * Get a wanderer factory
	 * 
	 * @param bc the sign that request the wanderer
	 * @param inv the inventory where to extract the wanderercontent from
	 * @return the wanderer factory
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public WandererFactory getFactory(Inventory inv) throws ClassNotFoundException, IOException;
	
	public void saveContent(InventoryContent rte) throws ClassNotFoundException, IOException;
}
