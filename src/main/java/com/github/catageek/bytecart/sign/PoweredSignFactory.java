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

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.hardware.AbstractIC;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;


/**
 * This class contains the method to instantiate any IC
 */
public class PoweredSignFactory {

    /**
     * Get an IC with the specific code
     *
     * @param block the block where to reference the IC
     * @param signString the name of the sign as "BCXXXX"
     * @return a Powerable IC, or null
     */
    static final public Powerable getPoweredIC(BlockSnapshot block, String signString) {

        if (signString.length() < 7) {
            return null;
        }

        int icNumber = Integer.parseInt(signString.substring(3, 7));

        try {

            // then we instantiate accordingly
            switch (icNumber) {

                case 7001:
                    return new BC7001(block, null);
                case 7003:
                    return new BC7003(block);
                case 7004:
                    return new BC7004(block, block.getState().get(Keys.SIGN_LINES).get().get(3).toPlain(),
                            block.getState().get(Keys.SIGN_LINES).get().get(2).toPlain());
                case 9001:
                    return new BC9001(block, null);


            }
        } catch (Exception e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }

            // there was no inventory in the cart
            return null;
        }
        return null;

    }

    /**
     * Get an IC at the powered sign
     *
     * @param block the sign clicked
     * @return a Powerable IC, or null
     */
    public static Powerable getIC(BlockSnapshot block) {


        if (AbstractIC.checkEligibility(block)) {

            // if there is really a BC sign post
            // we extract its #

            return PoweredSignFactory.getPoweredIC(block, block.getState().get(Keys.SIGN_LINES).get().get(1).toPlain());


        }
        // no BC sign post

        return null;

    }

}
