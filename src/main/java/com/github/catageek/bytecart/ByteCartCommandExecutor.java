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
package com.github.catageek.bytecart;

import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressRouted;
import com.github.catageek.bytecart.address.AddressString;
import com.github.catageek.bytecart.event.ByteCartInventoryListener;
import com.github.catageek.bytecart.event.ByteCartUpdaterMoveListener;
import com.github.catageek.bytecart.sign.BC7010;
import com.github.catageek.bytecart.sign.BC7011;
import com.github.catageek.bytecart.sign.BC7017;
import com.github.catageek.bytecart.thread.ModifiableRunnable;
import com.github.catageek.bytecart.updater.UpdaterContentFactory;
import com.github.catageek.bytecart.updater.UpdaterFactory;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.ContainerMinecart;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * The command executor
 */
public class ByteCartCommandExecutor {

    public static final CommandSpec MEGO = CommandSpec.builder()
            .arguments(GenericArguments.string(Text.of("destination")), GenericArguments.optional(GenericArguments.literal(Text.of("train"),
                    Text.of("train"))))
            .executor((source, context) -> {
                if (!(source instanceof Player)) {
                    Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                } else {
                    Player player = (Player) source;
                    String hostOrAddress;
                    boolean isTrain = false;
                    if (context.hasAny(Text.of("train"))) {
                        isTrain = true;
                        hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                    } else {
                        hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                    }

                    if (!AddressString.isResolvableAddressOrName(hostOrAddress)) {
                        Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidaddress").getString()));
                        return CommandResult.empty();
                    }

                    (new BC7010(player.getLocation().createSnapshot(), player)).setAddress(hostOrAddress, isTrain);
                }
                return CommandResult.success();
            }).build();

    public static final CommandSpec SENDTO = CommandSpec.builder()
            .arguments(GenericArguments.string(Text.of("destination")), GenericArguments.optional(GenericArguments.literal(Text.of("train"),
                    Text.of("train"))))
            .executor((source, context) -> {
                if (!(source instanceof Player)) {
                    Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                } else {
                    Player player = (Player) source;
                    String hostOrAddress;
                    boolean isTrain = false;
                    if (context.hasAny(Text.of("train"))) {
                        isTrain = true;
                        hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                    } else {
                        hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                    }

                    if (!AddressString.isResolvableAddressOrName(hostOrAddress)) {
                        Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidaddress").getString()));
                        return CommandResult.empty();
                    }

                    final class Execute implements ModifiableRunnable<Inventory> {

                        private final Player player;
                        private final String address;
                        private Inventory inventory;
                        private boolean isTrain;


                        public Execute(Player player, String hostOrAddress, boolean isTrain) {
                            this.player = player;
                            this.address = hostOrAddress;
                            this.isTrain = isTrain;
                        }

                        public void run() {
                            if ((new BC7011(player.getLocation().createSnapshot(),
                                    ((CarriedInventory<ContainerMinecart>) inventory).getCarrier().get()))
                                    .setAddress(address, this.isTrain)) {
                                Messaging.sendSuccess(player,
                                        Text.of(String.format(ByteCartRedux.rootNode.getNode("messages", "info", "setaddress").getString(),
                                                hostOrAddress)));
                                Messaging.sendSuccess(player,
                                        Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "getttl").getString()
                                                + AddressFactory.<AddressRouted>getAddress(((CarriedInventory<ContainerMinecart>) inventory))
                                                .getTTL()));
                            } else {
                                Messaging.sendError(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "setaddress").getString()));
                            }

                        }


                        /**
                         * @param inventory the inventory to set
                         */

                        @Override
                        public void setParam(Inventory inventory) {
                            this.inventory = inventory;
                        }

                    }

                    Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                    new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player, new Execute(player, hostOrAddress, isTrain), false);

                }
                return CommandResult.success();
            })
            .build();

    public static final CommandSpec BCRELOAD = CommandSpec.builder()
            .executor((source, context) -> {
                ByteCartRedux.myPlugin.loadConfig();

                Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "configreloaded").getString()));
                return CommandResult.success();
            }).build();

    /**
     * bcticket command.
     *
     * Usage: /bcticket destination [isTrain]
     *     OR /bcticket player destination [isTrain]
     */
    public static final CommandSpec BCTICKET = CommandSpec.builder()
            .arguments(GenericArguments.playerOrSource(Text.of("player")), GenericArguments.string(Text.of("destination")),
                    GenericArguments.optional(GenericArguments.literal(Text.of("train"), Text.of("train"))))
            .executor((source, context) -> {
                Optional<Player> player;
                String addressString;
                boolean isTrain = false;

                String hostOrAddress;
                if (context.hasAny(Text.of("train"))) {
                    isTrain = true;
                    hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                } else {
                    hostOrAddress = context.<String>getOne(Text.of("destination")).get();
                }

                player = context.getOne(Text.of("player"));
                addressString = hostOrAddress;

                if (!player.isPresent()) {
                    Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidplayer").getString()));
                    return CommandResult.empty();
                }

                if (!AddressString.isResolvableAddressOrName(addressString)) {
                    Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidaddress").getString()));
                    return CommandResult.empty();
                }

                (new BC7010(player.get().getLocation().createSnapshot(), player.get())).setAddress(addressString, isTrain);

                Messaging.sendSuccess(player.get(), Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "ticketcreated").getString()));

                return CommandResult.success();
            }).build();

    public static final CommandSpec BCBACK = CommandSpec.builder()
            .executor((source, context) -> {
                if (!(source instanceof Player)) {
                    Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                    return CommandResult.empty();
                }

                Player player = (Player) source;

                (new BC7017(player.getLocation().createSnapshot(), player)).trigger();

                Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "returnback").getString()));

                return CommandResult.success();
            }).build();

    public static final CommandSpec BCUPDATER = CommandSpec.builder()
            .child(CommandSpec.builder()
                    .executor((source, context) -> {
                        ByteCartRedux.myPlugin.getWandererManager().unregister("Updater");
                        ByteCartUpdaterMoveListener.clearUpdaters();
                        return CommandResult.success();
                    }).build(), "remove")
            .child(CommandSpec.builder()
                    .arguments(GenericArguments.integer(Text.of("region")), GenericArguments.optionalWeak(GenericArguments.bool(Text.of("new"))))
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;
                        int region = context.<Integer>getOne(Text.of("region")).orElse(0);
                        boolean isNew = context.<Boolean>getOne(Text.of("new")).orElse(false);

                        if (region < 1 || region > 2047) {
                            return CommandResult.empty();
                        }

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.REGION, region, false, isNew), true);

                        return CommandResult.success();
                    }).build(), "region")
            .child(CommandSpec.builder()
                    .arguments(GenericArguments.integer(Text.of("region")), GenericArguments.optionalWeak(GenericArguments.bool(Text.of("new"))))
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;
                        int region = context.<Integer>getOne(Text.of("region")).orElse(0);
                        boolean isNew = context.<Boolean>getOne(Text.of("new")).orElse(false);

                        if (region < 1 || region > 2047) {
                            return CommandResult.empty();
                        }

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.LOCAL, region, false, isNew), true);

                        return CommandResult.success();
                    }).build(), "local")
            .child(CommandSpec.builder()
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.BACKBONE, 0, false, false), true);

                        return CommandResult.success();
                    }).build(), "backbone")
            .child(CommandSpec.builder()
                    .arguments(GenericArguments.integer(Text.of("region")), GenericArguments.optionalWeak(GenericArguments.bool(Text.of("full"))))
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;
                        int region = context.<Integer>getOne(Text.of("region")).orElse(0);
                        boolean fullReset = context.<Boolean>getOne(Text.of("full")).orElse(false);

                        if (region < 1 || region > 2047) {
                            return CommandResult.empty();
                        }

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.RESET_REGION, region, fullReset, false), true);

                        return CommandResult.success();
                    }).build(), "reset_region")
            .child(CommandSpec.builder()
                    .arguments(GenericArguments.integer(Text.of("region")), GenericArguments.optionalWeak(GenericArguments.bool(Text.of("full"))))
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;
                        int region = context.<Integer>getOne(Text.of("region")).orElse(0);
                        boolean fullReset = context.<Boolean>getOne(Text.of("full")).orElse(false);

                        if (region < 1 || region > 2047) {
                            return CommandResult.empty();
                        }

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.RESET_LOCAL, region, fullReset, false), true);

                        return CommandResult.success();
                    }).build(), "reset_local")
            .child(CommandSpec.builder()
                    .arguments(GenericArguments.optionalWeak(GenericArguments.bool(Text.of("full"))))
                    .executor((source, context) -> {
                        if (!(source instanceof Player)) {
                            Messaging.sendError(source, Text.of(ByteCartRedux.rootNode.getNode("messages", "error", "invalidsource").getString()));
                            return CommandResult.empty();
                        }

                        if (!ByteCartRedux.myPlugin.getWandererManager().isWandererType("Updater")) {
                            ByteCartRedux.myPlugin.getWandererManager().register(new UpdaterFactory(), "Updater");
                        }

                        Player player = (Player) source;
                        boolean fullReset = context.<Boolean>getOne(Text.of("full")).orElse(false);

                        Messaging.sendSuccess(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "rightclickcart").getString()));
                        new ByteCartInventoryListener<>(ByteCartRedux.myPlugin, player,
                                new ExecuteUpdate(player, Wanderer.Level.RESET_BACKBONE, 0, fullReset, false), true);

                        return CommandResult.success();
                    }).build(), "reset_backbone")
            .build();

    private static final class ExecuteUpdate implements ModifiableRunnable<CarriedInventory<ContainerMinecart>> {

        private final Player player;
        private final Wanderer.Level level;
        private final int region;
        private final boolean isFullReset;
        private final boolean isNew;
        private CarriedInventory<ContainerMinecart> inventory;


        public ExecuteUpdate(Player player, Wanderer.Level level, int region, boolean isFullReset, boolean isNew) {
            this.player = player;
            this.level = level;
            this.region = region;
            this.isFullReset = isFullReset;
            this.isNew = isNew;
        }

        public void run() {
            UUID id = inventory.getCarrier().get().getUniqueId();
            try {
                UpdaterContentFactory.createRoutingTableExchange(inventory, region, level, player, isFullReset, isNew);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!ByteCartUpdaterMoveListener.isExist()) {
                ByteCartUpdaterMoveListener updatermove = new ByteCartUpdaterMoveListener();
                Sponge.getEventManager().registerListeners(ByteCartRedux.myPlugin, updatermove);
                ByteCartUpdaterMoveListener.setExist(true);
            }
            ByteCartUpdaterMoveListener.addUpdater(id);
            Messaging.sendError(player, Text.of(ByteCartRedux.rootNode.getNode("messages", "info", "setupdater").getString()));
        }


        /**
         * @param inventory the inventory to set
         */

        @Override
        public void setParam(CarriedInventory<ContainerMinecart> inventory) {
            this.inventory = inventory;
        }

    }

}
