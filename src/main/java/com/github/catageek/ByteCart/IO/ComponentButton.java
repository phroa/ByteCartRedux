/**
 * ByteCart, ByteCart Redux
 * Copyright (C) Catageek
 * Copyright (C) phroa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.catageek.ByteCart.IO;

import com.github.catageek.ByteCart.ByteCart;
import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Button;
import org.bukkit.material.MaterialData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A button
 */
public class ComponentButton extends AbstractComponent implements OutputPin, InputPin {

    final static private Map<Location, Integer> ActivatedButtonMap = new ConcurrentHashMap<Location, Integer>();

    /**
     * @param block the block containing the component
     */
    protected ComponentButton(Block block) {
        super(block);
/*		if(ByteCart.debug)
            ByteCart.log.info("ByteCart : adding Button at " + block.getLocation().toString());
*/
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.IO.OutputPin#write(boolean)
     */
    @Override
    public void write(boolean bit) {
        final Block block = this.getBlock();
        final BlockState blockstate = block.getState();
        if (blockstate.getData() instanceof Button) {
            final ComponentButton component = this;
            int id;

            final Button button = (Button) blockstate.getData();

            if (bit) {
                if (ActivatedButtonMap.containsKey(block)) {

                    // if button is already on, we cancel the scheduled thread
                    ByteCart.myPlugin.getServer().getScheduler().cancelTask(ActivatedButtonMap.get(block));

                    // and we reschedule one
                    id = ByteCart.myPlugin.getServer().getScheduler()
                            .scheduleSyncDelayedTask(ByteCart.myPlugin, new SetButtonOff(component, ActivatedButtonMap)
                                    , 40);

                    // We update the HashMap
                    ActivatedButtonMap.put(block.getLocation(), id);

                } else {
                    // if button is off, we power the button
                    button.setPowered(true);
                    blockstate.setData(button);
                    blockstate.update(false, true);
                    MathUtil.forceUpdate(this.getBlock().getRelative(button.getAttachedFace()));
			
			
/*			if(ByteCart.debug)
				ByteCart.log.info("Button at (" + this.getLocation().toString() + ") : " + bit);
*/


                    // delayed action to unpower the button after 2 s.

                    id = ByteCart.myPlugin.getServer().getScheduler()
                            .scheduleSyncDelayedTask(ByteCart.myPlugin, new SetButtonOff(component, ActivatedButtonMap)
                                    , 40);

                    // We update the HashMap
                    ActivatedButtonMap.put(block.getLocation(), id);
                }
            }

        }
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.IO.InputPin#read()
     */
    @Override
    public boolean read() {
        MaterialData md = this.getBlock().getState().getData();
        if (md instanceof Button) {
            return ((Button) md).isPowered();
        }
        return false;
    }


}
