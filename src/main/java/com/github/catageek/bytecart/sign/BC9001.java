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
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.collision.IntersectionSide;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.event.custom.SignPostStationEvent;
import com.github.catageek.bytecart.event.custom.SignPreStationEvent;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import com.github.catageek.bytecart.util.MathUtil;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;

import java.io.IOException;


/**
 * A station sign
 */
public final class BC9001 extends AbstractBC9000 implements Station, Powerable, Triggerable {


    BC9001(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
        this.netmask = 8;
    }

    @Override
    public void trigger() {
        try {

            Address sign = AddressFactory.getAddress(this.getBlock(), 3);

            this.addIO();

            // input[6] = redstone for "full station" signal

            InputPin[] wire = new InputPin[2];

            // Right
            wire[0] = InputPinFactory
                    .getInput(this.getBlock().getLocation().get().getRelative(Direction.UP).add(getCardinal().toVector3d().mul(2))
                            .getRelative(MathUtil.clockwise(getCardinal())).createSnapshot());
            // left
            wire[1] = InputPinFactory
                    .getInput(this.getBlock().getLocation().get().getRelative(Direction.UP).add(getCardinal().toVector3d().mul(2))
                            .getRelative(MathUtil.anticlockwise(getCardinal())).createSnapshot());

            // InputRegistry[0] = start/stop command
            this.addInputRegistry(new PinRegistry<>(wire));

            triggerBC7003();

            if (!WandererContentFactory.isWanderer(getInventory())) {

                // if this is a cart in a train
                if (this.wasTrain(this.getLocation())) {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getLocation());
                    return;
                }

                // if this is the first car of a train
                // we keep the state during 2 s
                if (AbstractTriggeredSign.isTrain(getDestinationAddress())) {
                    this.setWasTrain(this.getLocation(), true);
                }

                this.route();

                if (this.isAddressMatching() && this.getName().equals("BC9001") && this.getInventory() instanceof HumanInventory) {
                    Messaging.sendSuccess(((Player) ((HumanInventory) this.getInventory()).getCarrier().get()),
                            Text.of(String
                                    .format(ByteCartRedux.rootNode.getNode("messages", "info", "destination").getString(), this.getFriendlyName(),
                                            sign)));

                }
                return;
            }

            // it's an wanderer
            Wanderer wanderer;
            try {
                wanderer = ByteCartRedux.myPlugin.getWandererManager().getFactory(this.getInventory()).getWanderer(this, this.getInventory());
                // here we perform wanderer action
                wanderer.doAction(IntersectionSide.Side.LEVER_OFF);
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // routing
            this.getOutput(0).setAmount(0); // unpower levers


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

    @Override
    public void power() {
        this.powerBC7003();
    }


    /**
     * Manage the red light signal when triggered
     *
     */
    private void triggerBC7003() {
        (new BC7003(this.getBlock())).trigger();
    }

    /**
     * Manage the red light signal when powered
     *
     */
    private void powerBC7003() {
        (new BC7003(this.getBlock())).power();
    }


    @Override Side route() {
        SignPreStationEvent event;
        SignPostStationEvent event1;
        // test if every destination field matches sign field
        if (this.isAddressMatching() && this.getInput(6).getValue() == 0) {
            event = new SignPreStationEvent(this, Side.LEVER_ON); // power levers if matching
        } else {
            event = new SignPreStationEvent(this, Side.LEVER_OFF); // unpower levers if not matching
        }
        Sponge.getEventManager().post(event);

        if (event.getSide().equals(Side.LEVER_ON) && this.getInput(6).getValue() == 0) {
            this.getOutput(0).setAmount(3); // power levers if matching
            event1 = new SignPostStationEvent(this, Side.LEVER_ON);
        } else {
            this.getOutput(0).setAmount(0); // unpower levers if not matching
            event1 = new SignPostStationEvent(this, Side.LEVER_ON);
        }
        Sponge.getEventManager().post(event1);
        return null;
    }

    @Override
    public final String getName() {
        return "BC9001";
    }

    public final String getStationName() {
        return this.getBlock().getState().get(Keys.SIGN_LINES).get().get(2).toPlain();
    }
}
