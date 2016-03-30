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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;

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
    public static final Clickable getClickedIC(BlockSnapshot block, Player player) {


        if (AbstractIC.checkEligibility(block)) {

            // if there is really a BC sign post
            // we extract its #

            return ClickedSignFactory.getClickedIC(block, block.getState().get(Keys.SIGN_LINES).get().get(1).toPlain(), player);


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
    public static final Clickable getBackwardClickedIC(BlockSnapshot block, Player player) {
        BlockType type = block.getState().getType();
        if (type.equals(BlockTypes.STANDING_SIGN) || type.equals(BlockTypes.WALL_SIGN)) {
            Direction d = block.get(Keys.DIRECTION).get().getOpposite();
            d = MathUtil.straightUp(d);

            final BlockSnapshot relative = block.getLocation().get().add(d.toVector3d().mul(2)).createSnapshot(); //(d, 2);
            if (AbstractIC.checkEligibility(relative)) {
                return ClickedSignFactory.getClickedIC(relative, block.getState().get(Keys.SIGN_LINES).get().get(1).toPlain(), player);
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
    public static final Clickable getClickedIC(BlockSnapshot block, String signString, Player player) {

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
