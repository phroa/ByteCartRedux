package com.github.catageek.ByteCart.Wanderer;

import com.github.catageek.ByteCart.Util.DirectionRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.Serializable;
import java.util.Stack;


public interface InventoryContent extends Serializable {

    /**
     * @return the inventory
     */
    public Inventory getInventory();

    /**
     * Get the level of the wanderer
     *
     * @return the level
     */
    public Wanderer.Level getLevel();

    /**
     * Get the region of the wanderer
     *
     * @return the region
     */
    public int getRegion();

    /**
     * @return the player that creates the wanderer
     */
    public Player getPlayer();

    public Counter getCounter();

    public Stack<Integer> getStart();

    public Stack<Integer> getEnd();

    public boolean hasRouteTo(int ring);

    public int getMinDistanceRing(
            RoutingTable routingTable,
            DirectionRegistry from);

    public int getCurrent();

    public void setCurrent(int i);

    public int getMetric(int ring);

    public void setRoute(int ring, int i);

}
