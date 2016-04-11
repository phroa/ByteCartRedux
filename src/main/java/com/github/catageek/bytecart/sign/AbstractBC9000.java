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
import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.collision.SimpleCollisionAvoider;
import com.github.catageek.bytecart.event.custom.SignPostSubnetEvent;
import com.github.catageek.bytecart.event.custom.SignPreSubnetEvent;
import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.hardware.RegistryInput;
import com.github.catageek.bytecart.hardware.SubRegistry;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

import java.io.IOException;


/**
 * An abstract class for all subnet class
 */
abstract class AbstractBC9000 extends AbstractSimpleCrossroad implements Subnet, HasNetmask {

    int netmask;


    AbstractBC9000(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    @Override
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

                Side result = intersection.wishToGo(this.route(), isTrain);
                SignPostSubnetEvent event = new SignPostSubnetEvent(this, result);
                Sponge.getEventManager().post(event);
                return;
            }

            manageWanderer(intersection);

        } catch (ClassCastException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.myPlugin.getLog().info(e.toString());
            }

            // Not the good blocks to build the signs
        } catch (NullPointerException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.myPlugin.getLog().info(e.toString());
            }
            e.printStackTrace();

            // there was no inventory in the cart
        }

    }

    @Override void manageWanderer(SimpleCollisionAvoider intersection) {
        // it's an updater, so let it choosing direction
        Wanderer wanderer;
        try {
            wanderer = ByteCartRedux.myPlugin.getWandererManager().getFactory(this.getInventory()).getWanderer(this, this.getInventory());

            // routing
            Side to = intersection.wishToGo(wanderer.giveSimpleDirection(), false);

            // here we perform routes update
            wanderer.doAction(to);
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override Side route() {
        SignPreSubnetEvent event;
        AddressRouted dst = this.getDestinationAddress();
        int ttl;
        if (this.isAddressMatching() && (ttl = dst.getTTL()) != 0) {
            dst.updateTTL(ttl - 1);
            dst.finalizeAddress();
            event = new SignPreSubnetEvent(this, Side.LEVER_ON);
        } else {
            event = new SignPreSubnetEvent(this, Side.LEVER_OFF);
        }

        Sponge.getEventManager().post(event);
        return event.getSide();
    }

    /**
     * Get the first station of the subnet
     *
     * @param station a station number in the subnet
     * @return the first station number
     */
    private RegistryBoth applyNetmask(RegistryBoth station) {
        if (this.netmask < station.length()) {
            return new SubRegistry<>(station, this.netmask, 0);
        }
        return station;
    }

    /**
     * Tell if the address stored in the ticket is matching the subnet address stored in the IC
     *
     * @return true if the address is in the subnet
     */
    boolean isAddressMatching() {
        try {
            return this.getInput(2).getValue() == this.getInput(5).getValue()
                    && this.getInput(1).getValue() == this.getInput(4).getValue()
                    && this.getInput(0).getValue() == this.getInput(3).getValue();
        } catch (NullPointerException e) {
            // there is no address on sign
        }
        return false;
    }


    /*
     * Configures all IO ports of this sign.
     *
     * The following input pins are configured:
     * 0: vehicle region
     * 1: vehicle track
     * 2: vehicle station (w/ applied net mask)
     * 3: sign region
     * 4: sign track
     * 5: sign station (w/ applied net mask)
     *
     * The following output pins are configured:
     * 0: left lever
     * 1: right lever
     */
    @Override void addIO() {
        Address sign = this.getSignAddress();

        super.addIO();

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

        // Input[2] = destination station taken from cart, slot #2, 6 bits

        RegistryBoth slot0 = IPaddress.getStation();


        // We keep only the X most significant bits (netmask)

        slot0 = applyNetmask(slot0);

        this.addInputRegistry(slot0);


        // Address is on a sign, line #3
        // Input[3] = region from sign, line #3, 6 bits registry
        // Input[4] = track from sign, line #3, 6 bits registry
        // Input[5] = station number from sign, line #0, 6 bits registry
        this.addAddressAsInputs(sign);
    }

    /**
     * Register the given address as an input of the IC
     *
     * This method will register 3 inputs.
     *
     * @param addr the address to register
     */
    private void addAddressAsInputs(Address addr) {
        if (addr.isValid()) {
            RegistryInput region = addr.getRegion();
            this.addInputRegistry(region);

            RegistryInput track = addr.getTrack();
            this.addInputRegistry(track);

            RegistryBoth station = addr.getStation();
            station = applyNetmask(station);
            this.addInputRegistry(station);
        }
    }

    @Override
    public final int getNetmask() {
        return netmask;
    }


}
