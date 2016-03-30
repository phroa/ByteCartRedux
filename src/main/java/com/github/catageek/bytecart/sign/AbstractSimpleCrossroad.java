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

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collision.CollisionAvoiderBuilder;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.collision.SimpleCollisionAvoider;
import com.github.catageek.bytecart.collision.SimpleCollisionAvoiderBuilder;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.hardware.RegistryInput;
import com.github.catageek.bytecart.io.OutputPin;
import com.github.catageek.bytecart.io.OutputPinFactory;
import com.github.catageek.bytecart.util.MathUtil;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

/**
 * An abstract class for T-intersection signs
 */
abstract class AbstractSimpleCrossroad extends AbstractTriggeredSign implements BCSign {

    protected CollisionAvoiderBuilder builder;
    private AddressRouted destination;


    AbstractSimpleCrossroad(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        builder = new SimpleCollisionAvoiderBuilder(this, block.getLocation().get().add(this.getCardinal().toVector3d().mul(3))
                .createSnapshot()
                .getLocation().get());
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
        lever2[0] = OutputPinFactory.getOutput(this.getBlock().getLocation().get().getRelative(MathUtil.anticlockwise(this.getCardinal())).createSnapshot());
        // Right
        lever2[1] = OutputPinFactory.getOutput(this.getBlock().getLocation().get().getRelative(MathUtil.clockwise(this.getCardinal())).createSnapshot());

        PinRegistry<OutputPin> command1 = new PinRegistry<OutputPin>(lever2);

        this.addOutputRegistry(command1);
    }

    protected final void addIOInv() {
        // Input[0] = destination region taken from Inventory, slot #0


        Address destinationAddress = getDestinationAddress();

        if (destinationAddress == null) {
            return;
        }

        RegistryInput slot2 = destinationAddress.getRegion();


        this.addInputRegistry(slot2);

        // Input[1] = destination track taken from cart, slot #1

        RegistryInput slot1 = destinationAddress.getTrack();


        this.addInputRegistry(slot1);

        // Input[2] = destination station taken from cart, slot #2

        RegistryBoth slot0 = destinationAddress.getStation();

        this.addInputRegistry(slot0);
    }


    protected void manageWanderer(SimpleCollisionAvoider intersection) {
        // routing
        intersection.wishToGo(route(), false);
    }

    protected Side route() {
        return Side.LEVER_OFF;
    }

    public void trigger() {
        try {

            this.addIO();

            SimpleCollisionAvoider intersection = ByteCartRedux.myPlugin.getCollisionAvoiderManager().getCollisionAvoider(builder);

            if (!WandererContentFactory.isWanderer(getInventory())) {

                boolean isTrain = AbstractTriggeredSign.isTrain(getDestinationAddress());

                // if this is a cart in a train
                if (this.wasTrain(this.getLocation())) {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getBlock().getLocation().get());
                    intersection.book(isTrain);
                    return;
                }

                // if this is the first car of a train
                // we keep it during 2 s
                if (isTrain) {
                    this.setWasTrain(this.getLocation(), true);
                }

                intersection.wishToGo(this.route(), isTrain);
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
    public final BlockSnapshot getCenter() {
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
