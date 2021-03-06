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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Direction;

import java.io.IOException;


/**
 * This class contains the method to instantiate any IC
 */
final public class TriggeredSignFactory {

    /**
     * Check the sign and instantiate the IC object, or null
     *
     * @param block the sign block
     * @param vehicle the vehicle that triggered the sign
     * @return a Triggerable representing the IC
     * @throws ClassNotFoundException
     * @throws IndexOutOfBoundsException
     * @throws IOException
     */
    static public Triggerable getTriggeredIC(BlockSnapshot block, Entity vehicle) throws ClassNotFoundException, IndexOutOfBoundsException,
            IOException {

        if (AbstractIC.checkEligibility(block)) {

            // if there is really a BC sign post
            // we extract its #

            return TriggeredSignFactory.getTriggeredIC(block, block.get(Keys.SIGN_LINES).get().get(1).toPlain(), vehicle);
        }

        // Maybe the rail is in slope
        BlockSnapshot block2 = block.getLocation().get().getRelative(Direction.DOWN).createSnapshot();
        if (AbstractIC.checkEligibility(block2)) {
            BlockSnapshot rail = block.getLocation().get().getRelative(Direction.UP).createSnapshot();
            if (rail.supports(Keys.RAIL_DIRECTION) && BC7001.ascending(rail.get(Keys.RAIL_DIRECTION).get())) {
                return TriggeredSignFactory.getTriggeredIC(block2, block2.get(Keys.SIGN_LINES).get().get(1).toPlain(), vehicle);
            }
        }
        // no BC sign post

        return null;
    }

    /**
     * Read the string and instantiate the IC object, or null
     *
     * The string must be checked before calling this method
     *
     * @param block the sign block
     * @param signString the string containing the IC number
     * @param vehicle the vehicle triggering the sign
     * @return a Triggerable representing the IC
     * @throws ClassNotFoundException
     * @throws IOException
     */
    static public Triggerable getTriggeredIC(BlockSnapshot block, String signString, Entity vehicle) throws ClassNotFoundException, IOException {

        if (signString.length() < 7) {
            return null;
        }

        int ICnumber;
        try {
            ICnumber = Integer.parseInt(signString.substring(3, 7));

            // then we instantiate accordingly
            switch (ICnumber) {

                case 7000:
                case 7001:
                    return new BC7001(block, vehicle);
                case 7002:
                    return new BC7002(block, vehicle);
                case 7003:
                    return new BC7003(block);
                case 7005:
                    return new BC7005(block, vehicle);
                case 7006:
                    return new BC7006(block, vehicle);
                case 7007:
                    return new BC7007(block, vehicle);
                case 7008:
                    return new BC7008(block, vehicle);
                case 7009:
                    return new BC7009(block, vehicle);
                case 7010:
                    return new BC7010(block, vehicle);
                case 7011:
                    return new BC7011(block, vehicle);
                case 7012:
                    return new BC7012(block, vehicle);
                case 7013:
                    return new BC7013(block, vehicle);
                case 7014:
                    return new BC7014(block, vehicle);
                case 7015:
                    return new BC7015(block, vehicle);
                case 7016:
                    return new BC7016(block, vehicle);
                case 7017:
                    return new BC7017(block, vehicle);
                case 7018:
                    return new BC7018(block, vehicle);
                case 7019:
                    return new BC7019(block, vehicle);
                case 7020:
                    return new BC7020(block, vehicle);
                case 7021:
                    return new BC7021(block, vehicle);

                case 8010:
                    return new BC8010(block, vehicle);

                case 8020:
                    return new BC8020(block, vehicle);

                case 9000:
                    return new BC9000(block, vehicle);
                case 9001:
                    return new BC9001(block, vehicle);
                case 9002:
                    return new BC9002(block, vehicle);
                case 9004:
                    return new BC9004(block, vehicle);
                case 9008:
                    return new BC9008(block, vehicle);
                case 9016:
                    return new BC9016(block, vehicle);
                case 9032:
                    return new BC9032(block, vehicle);
                case 9064:
                    return new BC9064(block, vehicle);
                case 9128:
                    return new BC9128(block, vehicle);
                case 9037:
                    return (new BC9037(block, vehicle));
                case 9137:
                    return (new BC9137(block, vehicle));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
