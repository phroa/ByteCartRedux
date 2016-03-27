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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * This class contains the method to instantiate any IC
 */
final public class ClickedSignFactory {


    /**
     * Get an IC at the clicked sign
     *
     * @param block the sign clicked
     * @param player the player who clicked the sign
     * @return a Clickable IC, or null
     */
    static final public Clickable getClickedIC(Block block, Player player) {


        if (AbstractIC.checkEligibility(block)) {

            // if there is really a BC sign post
            // we extract its #

            return ClickedSignFactory.getClickedIC(block, ((Sign) block.getState()).getLine(1), player);


        }
        // no BC sign post

        return null;

    }

    /**
     * Get an IC with a code declared 2 blocks behind the clicked sign
     *
     * @param block the sign clicked
     * @param player the player who clicked the sign
     * @return a Clickable IC, or null
     */
    static final public Clickable getBackwardClickedIC(Block block, Player player) {
        Material type = block.getState().getType();
        if (type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
            BlockFace f = ((org.bukkit.material.Sign) block.getState().getData()).getFacing().getOppositeFace();
            f = MathUtil.straightUp(f);

            final Block relative = block.getRelative(f, 2);
            if (AbstractIC.checkEligibility(relative)) {
                return ClickedSignFactory.getClickedIC(relative, ((Sign) relative.getState()).getLine(1), player);
            }
        }
        return null;
    }


    /**
     * Get an IC with the specific code
     *
     * @param block the block where to reference the IC
     * @param signString the name of the sign as "BCXXXX"
     * @param player the player who clicked the sign
     * @return a Clickable IC, or null
     */
    static final public Clickable getClickedIC(Block block, String signString, Player player) {

        if (signString.length() < 7) {
            return null;
        }

        int ICnumber = Integer.parseInt(signString.substring(3, 7));

        // then we instantiate accordingly
        switch (ICnumber) {

            case 7010:
                return new BC7010(block, player);
            case 7018:
                return new BC7018(block, player);
        }

        return null;

    }
}