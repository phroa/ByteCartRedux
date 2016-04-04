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
import com.github.catageek.bytecart.collision.SimpleCollisionAvoider;
import com.github.catageek.bytecart.updater.UpdaterLocal;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

import java.io.IOException;


/**
 * A simple intersection block with anticollision
 */
final class BC9000 extends AbstractSimpleCrossroad implements Subnet, Triggerable {

    private final int netmask;

    BC9000(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        this.netmask = 0;
    }

    @Override
    protected void manageWanderer(SimpleCollisionAvoider intersection) {
        // it's an updater, so let it choosing direction
        super.manageWanderer(intersection);

        if (ByteCartRedux.rootNode.getNode("oldBC9000behaviour").getBoolean(true)) {
            UpdaterLocal updater;
            try {
                updater =
                        (UpdaterLocal) ByteCartRedux.myPlugin.getWandererManager().getFactory(this.getInventory())
                                .getWanderer(this, this.getInventory());

                // here we perform routes update
                updater.leaveSubnet();
                updater.save();

            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return "BC9000";
    }

    @Override
    public String getFriendlyName() {
        return "Collision avoider";
    }

    /**
     * @return the netmask
     */
    public final int getNetmask() {
        return netmask;
    }
}
