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

import com.github.catageek.bytecart.collection.IsTrainManager;
import com.github.catageek.bytecart.collision.CollisionAvoiderManager;
import com.github.catageek.bytecart.event.ByteCartListener;
import com.github.catageek.bytecart.event.ConstantSpeedListener;
import com.github.catageek.bytecart.event.PreloadChunkListener;
import com.github.catageek.bytecart.updater.BCWandererManager;
import com.github.catageek.bytecart.updater.UpdaterFactory;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Main class
 */
@Plugin(id = "com.github.catageek.bytecart",
        name = "ByteCartRedux",
        description = "Minecart routing system",
        version = "3.0.0.4",
        url = "https://github.com/phroa/ByteCartRedux")
public final class ByteCartRedux {

    @Inject
    public static Logger log;
    public static CommentedConfigurationNode rootNode;
    public static ByteCartRedux myPlugin;
    public static boolean debug;
    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
    private PreloadChunkListener preloadChunkListener;
    private ConstantSpeedListener constantSpeedListener;
    private CollisionAvoiderManager cam;
    private BCWandererManager wf;
    private IsTrainManager it;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) throws IOException {
        myPlugin = this;
        rootNode = configurationLoader.load();

        if (rootNode.getNode("debug").isVirtual()) {
            saveDefaultConfig();
        }
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        this.loadConfig();

        this.cam = new CollisionAvoiderManager();
        this.wf = new BCWandererManager();
        this.it = new IsTrainManager();

        Sponge.getEventManager().registerListeners(this, new ByteCartListener());

        // register updater factory
        if (!this.getWandererManager().isWandererType("Updater")) {
            this.getWandererManager().register(new UpdaterFactory(), "Updater");
        }

        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.MEGO, "mego");
        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.SENDTO, "sendto");
        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.BCRELOAD, "bcreload");
        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.BCUPDATER, "bcupdater");
        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.BCTICKET, "bcticket");
        Sponge.getCommandManager().register(this, ByteCartCommandExecutor.BCBACK, "bcback");
        log.info("[ByteCartRedux] plugin has been enabled.");
    }

    @Listener
    public void onStoppingServer(GameStoppingServerEvent event) {
        try {
            configurationLoader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the configuration file
     *
     */
    final void loadConfig() {
        debug = rootNode.getNode("debug").getBoolean(false);

        if (debug) {
            log.info("ByteCartRedux : debug mode is on.");
        }

        if (rootNode.getNode("loadchunks").getBoolean()) {
            if (preloadChunkListener == null) {
                preloadChunkListener = new PreloadChunkListener();
                Sponge.getEventManager().registerListeners(this, preloadChunkListener);
            }
        } else if (preloadChunkListener != null) {
            Sponge.getEventManager().unregisterListeners(preloadChunkListener);
            preloadChunkListener = null;
        }

        if (rootNode.getNode("constantspeed").getBoolean()) {
            if (constantSpeedListener == null) {
                constantSpeedListener = new ConstantSpeedListener();
                Sponge.getEventManager().registerListeners(this, constantSpeedListener);
            }
        } else if (constantSpeedListener != null) {
            Sponge.getEventManager().unregisterListeners(constantSpeedListener);
            constantSpeedListener = null;
        }
    }

    private void saveDefaultConfig() throws IOException {
        rootNode.getNode("book", "author").setComment("The author for ByteCartRedux-created books").setValue("ByteCart");
        rootNode.getNode("book", "mustprovide").setComment("Whether players are required to provide empty books in their inventory to use as tickets")
                .setValue(false);
        rootNode.getNode("book", "reuse").setComment("Whether books should be overwritten when players and carts change destinations").setValue(true);
        rootNode.getNode("book", "title").setComment("The string to prefix book titles with. If present, should not end with a space.")
                .setValue("ByteCart -");
        rootNode.getNode("book", "ttl").setComment(
                "The default TTL, or maximum number of routers to cross when finding a suitable route before getting sent to the default route")
                .setValue(64);
        rootNode.getNode("book", "use").setComment(
                "Whether to use books as tickets for players. Note - this means players can't travel if they do not have an empty inventory slot "
                        + "for the book.")
                .setValue(true);
        rootNode.getNode("constantspeed").setComment("Whether ByteCart-controlled minecarts should always travel at their maximum speed")
                .setValue(true);
        rootNode.getNode("debug").setComment("Whether to print debugging information to the console").setValue(false);
        rootNode.getNode("defaultroute").setComment(
                "Please note that \"0.0.0\" is a valid value, not just a placeholder. These addresses should always point at cart destroyers. This "
                        + "is also the final destination for region updater carts when they have finished.");
        rootNode.getNode("defaultroute", "empty").setComment("The default route of empty and storage carts without a destination").setValue("0.0.0");
        rootNode.getNode("defaultroute", "player").setComment("The default route of players without a destination").setValue("0.0.0");
        rootNode.getNode("loadchunks").setComment("Whether to force load chunks that ByteCart carts would travel through").setValue(true);
        rootNode.getNode("messages", "prefix")
                .setComment("Prefix to place before all user-visible messages. If set, be sure to include a trailing space.").setValue("[ByteCart] ");
        rootNode.getNode("messages", "error", "invalidaddress").setValue("Invalid destination address.");
        rootNode.getNode("messages", "error", "invalidplayer").setValue("Invalid player.");
        rootNode.getNode("messages", "error", "invalidsource").setValue("You must be a player to use this command.");
        rootNode.getNode("messages", "error", "inventoryspace").setValue("You must have an empty slot in your inventory to use this.");
        rootNode.getNode("messages", "error", "needbook").setValue("You must have a blank book in your inventory to use this.");
        rootNode.getNode("messages", "error", "permission").setComment("%s is a placeholder for the permission required")
                .setValue("You need the %s permission to do this.");
        rootNode.getNode("messages", "error", "subnetfull").setComment(
                "First %s is the location in the world of the sign. %d is the number of stations in the subnet. Second %s is the subnet number.")
                .setValue("Could not assign an address to the sign at %s. The %d-station subnet (%s) is full.");
        rootNode.getNode("messages", "error", "unauthorizedplace")
                .setComment("%s is the placeholder for the type of object. For example, \"L1 Router\"")
                .setValue("You don't have permission to create a(n) %s sign.");
        rootNode.getNode("messages", "info", "configreloaded").setComment("Configuration reloaded.");
        rootNode.getNode("messages", "info", "created").setComment("%s is the placeholder for the type of object. For example, \"L1 Router\"")
                .setValue("Created a(n) %s.");
        rootNode.getNode("messages", "info", "destination")
                .setComment("First %s is a placeholder for the \"friendly name\" of a station. Second %s is a placeholder for the station's address.")
                .setValue("Arrived at \"%s\" (%s).");
        rootNode.getNode("messages", "info", "getttl").setValue("Will cross up to %d routers.");
        rootNode.getNode("messages", "info", "returnback").setValue("Returning to origin.");
        rootNode.getNode("messages", "info", "rightclickcart").setValue("Please right-click a storage cart.");
        rootNode.getNode("messages", "info", "setaddress").setComment("%s is a placeholder for the new destination.").setValue("Destination set: %s");
        rootNode.getNode("messages", "info", "setaddress2").setValue("Please leave the ticket in your inventory.");
        rootNode.getNode("messages", "info", "setreturnaddress").setValue("Return address set.");
        rootNode.getNode("messages", "info", "setupdater").setValue("Updater created.");
        rootNode.getNode("messages", "info", "ticketcreated").setValue("Ticket created.");
        rootNode.getNode("messages", "info", "updaterexpired").setComment("%s is a placeholder for the date the now-expired updater was created")
                .setValue("Updater created at %s has expired.");
        rootNode.getNode("sign", "bc7001", "velocity").setComment(
                "The new velocity to boost carts to. This value should be between 0 and 1, inclusive. The vanilla maximum speed of a non-empty cart"
                        + " is 0.4.")
                .setValue(0.68);
        rootNode.getNode("sign", "bc7003", "lockduration").setComment("The number of ticks to wait before processing another cart").setValue(44);
        rootNode.getNode("sign", "bc7008", "keepitems").setComment("Whether carts destroyed by this sign will drop their items on the ground")
                .setValue(true);
        rootNode.getNode("sign", "bc9000", "oldbehavior")
                .setComment("Whether this sign can only be used to close a subtrack. If true, this sign must be used only to close subtracks.")
                .setValue(false);
        rootNode.getNode("sign", "fixbroken18").setComment("Whether to allow signs without brackets (fixes a bug in 1.8)").setValue(false);
        rootNode.getNode("updater", "timeout").setComment("The number of minutes updaters should stay around before timing out").setValue(60);
        configurationLoader.save(rootNode);
        rootNode = configurationLoader.load();
    }


    /**
     * @return the cam
     */
    public CollisionAvoiderManager getCollisionAvoiderManager() {
        return cam;
    }

    /**
     * @return the it
     */
    public IsTrainManager getIsTrainManager() {
        return it;
    }

    public final Logger getLog() {
        return log;
    }

    /**
     * @return the wf
     */
    public BCWandererManager getWandererManager() {
        return wf;
    }

}
