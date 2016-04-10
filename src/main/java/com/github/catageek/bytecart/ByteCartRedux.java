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
        version = "3.0.0.2",
        url = "https://github.com/phroa/ByteCartRedux")
public final class ByteCartRedux implements ByteCartPlugin {

    @Inject
    public static Logger log;
    public static CommentedConfigurationNode rootNode;
    public static ByteCartRedux myPlugin;
    public static boolean debug;
    public int lockDuration;
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
    private boolean keepItems;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        myPlugin = this;
        try {
            rootNode = configurationLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        this.loadConfig();

        this.setCam(new CollisionAvoiderManager());
        this.setWf(new BCWandererManager());
        this.setIt(new IsTrainManager());

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
        keepItems = rootNode.getNode("keepitems").getBoolean(true);

        lockDuration = rootNode.getNode("lockduration").getInt(44);

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


    /**
     * @return the cam
     */
    public CollisionAvoiderManager getCollisionAvoiderManager() {
        return cam;
    }

    /**
     * @param cam the cam to set
     */
    private void setCam(CollisionAvoiderManager cam) {
        this.cam = cam;
    }

    /**
     * @return the it
     */
    public IsTrainManager getIsTrainManager() {
        return it;
    }

    /**
     * @param it the it to set
     */
    private void setIt(IsTrainManager it) {
        this.it = it;
    }

    /**
     * @return true if we must keep items while removing carts
     */
    public boolean keepItems() {
        return keepItems;
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

    @Override
    public File getDataFolder() {
        return configDir;
    }

    /**
     * @param wf the wf to set
     */
    private void setWf(BCWandererManager wf) {
        this.wf = wf;
    }
}
