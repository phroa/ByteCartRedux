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
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.AddressLayer.Address;
import com.github.catageek.ByteCart.AddressLayer.AddressFactory;
import com.github.catageek.ByteCart.AddressLayer.AddressRouted;
import com.github.catageek.ByteCart.ByteCartRedux;
import com.github.catageek.ByteCart.CollisionManagement.CollisionAvoiderBuilder;
import com.github.catageek.ByteCart.CollisionManagement.IntersectionSide.Side;
import com.github.catageek.ByteCart.CollisionManagement.SimpleCollisionAvoider;
import com.github.catageek.ByteCart.CollisionManagement.SimpleCollisionAvoiderBuilder;
import com.github.catageek.ByteCart.HAL.PinRegistry;
import com.github.catageek.ByteCart.HAL.RegistryBoth;
import com.github.catageek.ByteCart.HAL.RegistryInput;
import com.github.catageek.ByteCart.IO.OutputPin;
import com.github.catageek.ByteCart.IO.OutputPinFactory;
import com.github.catageek.ByteCart.Util.MathUtil;
import com.github.catageek.ByteCart.Wanderer.Wanderer;
import com.github.catageek.ByteCart.Wanderer.WandererContentFactory;

/**
 * An abstract class for T-intersection signs
 */
abstract class AbstractSimpleCrossroad extends AbstractTriggeredSign implements BCSign {

    protected CollisionAvoiderBuilder builder;
    private AddressRouted destination;


    AbstractSimpleCrossroad(org.bukkit.block.Block block,
            org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        builder = new SimpleCollisionAvoiderBuilder((Triggable) this, block.getRelative(this.getCardinal(), 3).getLocation());
    }

    @Override
    abstract public String getName();

    /**
     * Register the inputs and outputs
     *
     */
    protected void addIO() {
        // Output[0] = 2 bits registry representing levers on the left and on the right of the sign
        OutputPin[] lever2 = new OutputPin[2];

        // Left
        lever2[0] = OutputPinFactory.getOutput(this.getBlock().getRelative(MathUtil.anticlockwise(this.getCardinal())));
        // Right
        lever2[1] = OutputPinFactory.getOutput(this.getBlock().getRelative(MathUtil.clockwise(this.getCardinal())));

        PinRegistry<OutputPin> command1 = new PinRegistry<OutputPin>(lever2);

        this.addOutputRegistry(command1);
    }

    protected final void addIOInv() {
        // Input[0] = destination region taken from Inventory, slot #0


        Address IPaddress = getDestinationAddress();

        if (IPaddress == null) {
            return;
        }

        RegistryInput slot2 = IPaddress.getRegion();


        this.addInputRegistry(slot2);

        // Input[1] = destination track taken from cart, slot #1

        RegistryInput slot1 = IPaddress.getTrack();


        this.addInputRegistry(slot1);

        // Input[2] = destination station taken from cart, slot #2

        RegistryBoth slot0 = IPaddress.getStation();

        this.addInputRegistry(slot0);
    }


    protected void manageWanderer(SimpleCollisionAvoider intersection) {
        // routing
        intersection.WishToGo(route(), false);
    }

    protected Side route() {
        return Side.LEVER_OFF;
    }

    public void trigger() {
        try {

            this.addIO();

            SimpleCollisionAvoider intersection = ByteCartRedux.myPlugin.getCollisionAvoiderManager().<SimpleCollisionAvoider>getCollisionAvoider(builder);

            if (!WandererContentFactory.isWanderer(getInventory())) {

                boolean isTrain = AbstractTriggeredSign.isTrain(getDestinationAddress());

                // if this is a cart in a train
                if (this.wasTrain(this.getLocation())) {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getBlock().getLocation());
                    intersection.Book(isTrain);
                    return;
                }

                // if this is the first car of a train
                // we keep it during 2 s
                if (isTrain) {
                    this.setWasTrain(this.getLocation(), true);
                }

                intersection.WishToGo(this.route(), isTrain);
                return;
            }

            manageWanderer(intersection);

        } catch (ClassCastException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }

            // Not the good blocks to build the signs
            return;
        } catch (NullPointerException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }
            e.printStackTrace();

            // there was no inventory in the cart
            return;
        }

    }

    protected final AddressRouted getDestinationAddress() {
        if (destination != null) {
            return destination;
        }
        return destination = AddressFactory.getAddress(this.getInventory());
    }

    @Override
    public Wanderer.Level getLevel() {
        return Wanderer.Level.LOCAL;
    }

    @Override
    public final Address getSignAddress() {
        return AddressFactory.getAddress(getBlock(), 3);
    }

    @Override
    public final org.bukkit.block.Block getCenter() {
        return this.getBlock();
    }

    @Override
    public final String getDestinationIP() {
        Address ip;
        if ((ip = getDestinationAddress()) != null) {
            return ip.toString();
        }
        return "";
    }
}
