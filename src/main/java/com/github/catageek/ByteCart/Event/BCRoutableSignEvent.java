package com.github.catageek.ByteCart.Event;

import com.github.catageek.ByteCart.HAL.IC;
import com.github.catageek.ByteCart.Signs.BCSign;
import com.github.catageek.ByteCart.Wanderer.Wanderer.Level;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


/**
 * A sign event. Implementations must inherit this class
 */
public abstract class BCRoutableSignEvent extends BCEvent {

    public BCRoutableSignEvent(IC ic) {
        super(ic);
    }


    abstract protected BCSign getSign();


    /**
     * Get the level of the sign.
     *
     * Possible values are:
     * 	- LOCAL for all BC9XXX signs
     *  - REGION for a BC8010 router
     *  - BACKBONE for a BC8020 router
     *
     * @return One value from Routing.Wanderer.Level enum
     */
    public final Level getLevel() {
        return getSign().getLevel();
    }

    /**
     * The block containing the sign.
     *
     * For routers, this gives the chest above the router
     *
     * @return A Location containing position of sign
     */
    public final Block getBlock() {
        return getSign().getCenter();
    }

    /**
     * Get the string on the 3rd line of the sign.
     * This is where users can put a name.
     *
     * @return The content of the 3rd line of the sign
     */
    public final String getFriendlyName() {
        return getSign().getFriendlyName();
    }

    /**
     * Get the string on the 2nd line of the sign
     * without the brackets
     *
     * @return The content of the 2nd line of the sign
     */
    public final String getName() {
        return getSign().getName();
    }

    public final BlockFace getDirection() {
        return getSign().getCardinal();
    }
}
