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
package com.github.catageek.bytecart.event;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.event.custom.SignCreateEvent;
import com.github.catageek.bytecart.event.custom.SignRemoveEvent;
import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.hardware.IC;
import com.github.catageek.bytecart.sign.Clickable;
import com.github.catageek.bytecart.sign.ClickedSignFactory;
import com.github.catageek.bytecart.sign.Powerable;
import com.github.catageek.bytecart.sign.PoweredSignFactory;
import com.github.catageek.bytecart.sign.Triggerable;
import com.github.catageek.bytecart.sign.TriggeredSignFactory;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.Minecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The main listener
 */
public class ByteCartListener {

    /**
     * Remove sign from cache and launch event
     *
     * @param block the sign
     * @param entity the entity at origin of the event
     */
    private static void removeSignIfNeeded(BlockSnapshot block, Entity entity) {
        if (!block.getState().supports(Keys.SIGN_LINES)) {
            return;
        }

        IC myIC;
        try {
            myIC = TriggeredSignFactory.getTriggeredIC(block, null);

            if (myIC == null) {
                myIC = ClickedSignFactory.getClickedIC(block, null);
            }

            if (myIC != null) {
                Sponge.getEventManager().post(new SignRemoveEvent(myIC, entity));
                AbstractIC.removeFromCache(block);
            }
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Detect if a sign is under the cart moving
     */
    @Listener
    public void onVehicleMove(DisplaceEntityEvent.Move event, @Root Minecart vehicle) throws Exception {
        Location<World> loc = event.getFromTransform().getLocation();
        Integer fromX = loc.getBlockX();
        Integer fromZ = loc.getBlockZ();
        loc = event.getToTransform().getLocation();
        int toX = loc.getBlockX();
        int toZ = loc.getBlockZ();


        // Check if the vehicle crosses a cube boundary
        if (fromX == toX && fromZ == toZ) {
            return;    // no boundary crossed, resumed
        }
        // we instantiate a member of the BCXXXX class
        // XXXX is read from the sign

        Triggerable myIC;
        try {
            myIC = TriggeredSignFactory
                    .getTriggeredIC(event.getToTransform().getLocation().add(Direction.DOWN.toVector3d().mul(2)).createSnapshot(), vehicle);
            if (myIC != null) {
                myIC.trigger();
            }
        } catch (ClassNotFoundException | IOException | IndexOutOfBoundsException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }


    /**
     * Detect a sign under the cart created
     */
    @Listener
    public void onVehicleCreate(SpawnEntityEvent event) throws Exception {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Minecart) // we care only of minecart
            {

                Minecart vehicle = (Minecart) entity;
                // we instantiate a member of the BCXXXX class
                // XXXX is read from the sign

                Triggerable myIC;
                try {
                    myIC = TriggeredSignFactory
                            .getTriggeredIC(vehicle.getLocation().add(Direction.DOWN.toVector3d().mul(2)).createSnapshot(), vehicle);
                    if (myIC != null) {
                        myIC.trigger();
                    }
                } catch (ClassNotFoundException | IOException | IndexOutOfBoundsException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * Detect if we create a sign
     */
    @Listener
    public void onSignChange(ChangeSignEvent event) throws Exception {
        List<Text> lines = event.getText().lines().get();
        if (!AbstractIC.checkEligibility(lines.get(1).toPlain())) {
            return;
        }

        BlockSnapshot block = event.getTargetTile().getBlock().snapshotFor(event.getTargetTile().getLocation());
        AbstractIC.removeFromCache(block);

        try {
            IC myIC = TriggeredSignFactory.getTriggeredIC(block, lines.get(1).toPlain(), null);

            if (myIC == null) {
                myIC = ClickedSignFactory.getClickedIC(block, lines.get(1).toPlain(), event.getCause().first(Player.class).get());
            }

            if (myIC == null) {
                myIC = PoweredSignFactory.getPoweredIC(block, lines.get(1).toPlain());
            }

            if (myIC != null) {
                Player player = event.getCause().first(Player.class).get();
                if (!player.hasPermission(myIC.getBuildPermission())) {
                    Messaging.sendError(player, Text.of(
                            String.format(ByteCartRedux.rootNode.getNode("messages", "error", "unauthorizedplace").getString(),
                                    myIC.getFriendlyName())));
                    Messaging.sendError(player, Text.of(
                            String.format(ByteCartRedux.rootNode.getNode("messages", "error", "permission").getString(),
                                    myIC.getBuildPermission())));
                    event.getText().addElement(1, Text.EMPTY);
                } else {
                    Messaging.sendError(player, Text.of(
                            String.format(ByteCartRedux.rootNode.getNode("messages", "info", "created").getString(),
                                    myIC.getFriendlyName())));
                    if (lines.get(2).toPlain().compareTo("") == 0) {
                        event.getText().addElement(2, Text.of(myIC.getFriendlyName()));
                    }
                    Sponge.getEventManager().post(new SignCreateEvent(myIC, player, lines.stream()
                            .map(Text::toPlain)
                            .collect(Collectors.toList())
                            .toArray(new String[4])));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if a sign was broken
     */
    @Listener(order = Order.POST)
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            removeSignIfNeeded(transaction.getFinal(), event.getCause().first(Entity.class).orElse(null));
        }
    }

    @Listener(order = Order.POST)
    public void onBlockBreak(ChangeBlockEvent.Modify event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            removeSignIfNeeded(transaction.getFinal(), event.getCause().first(Entity.class).orElse(null));

            if (!transaction.getFinal().getState().getType().equals(BlockTypes.REDSTONE_WIRE) || !AbstractIC
                    .checkEligibility(transaction.getFinal().getLocation().get().getRelative(Direction.DOWN).createSnapshot())) {
                return;
            }

            Powerable myIC = PoweredSignFactory.getIC(transaction.getFinal().getLocation().get().getRelative(Direction.DOWN).createSnapshot());


            if (myIC != null) {
                try {
                    myIC.power();
                } catch (ClassNotFoundException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Detect a sign that a player right-clicks
     */
    @Listener
    public void onPlayerInteract(InteractBlockEvent.Secondary event, @Root Player player) {

        Clickable myIC = ClickedSignFactory.getClickedIC(event.getTargetBlock(), player);

        if (myIC == null) {
            myIC = ClickedSignFactory.getBackwardClickedIC(event.getTargetBlock(), player);
        }

        if (myIC != null) {

            if (ByteCartRedux.debug) {
                ByteCartRedux.myPlugin.getLog().info("" + myIC.getName() + ".click()");
            }

            myIC.click();
            event.setCancelled(true);
        }
    }
}
