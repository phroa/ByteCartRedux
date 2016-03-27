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
package com.github.catageek.ByteCart;

import com.github.catageek.ByteCart.AddressLayer.Resolver;
import com.github.catageek.ByteCart.CollisionManagement.CollisionAvoiderManager;
import com.github.catageek.ByteCart.EventManagement.ByteCartListener;
import com.github.catageek.ByteCart.EventManagement.ConstantSpeedListener;
import com.github.catageek.ByteCart.EventManagement.PreloadChunkListener;
import com.github.catageek.ByteCart.Storage.IsTrainManager;
import com.github.catageek.ByteCart.Updaters.UpdaterFactory;
import com.github.catageek.ByteCart.Wanderer.BCWandererManager;
import com.github.catageek.ByteCart.plugins.BCHostnameResolutionPlugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Main class
 */
@Plugin(id = "ByteCartRedux", name = "ByteCartRedux Redux", version = "3.0.0.0")
public final class ByteCartRedux implements ByteCartPlugin {

    @Inject
    public static Logger log;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
    public static CommentedConfigurationNode rootNode;

    public static ByteCartRedux myPlugin;
    public static boolean debug;
    public int Lockduration;
    private BCHostnameResolutionPlugin hostnamePlugin;
    private PreloadChunkListener preloadchunklistener;
    private ConstantSpeedListener constantspeedlistener;
    private CollisionAvoiderManager cam;
    private BCWandererManager wf;
    private IsTrainManager it;
    private boolean keepitems;
    private Resolver resolver;

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

        ByteCartAPI.setPlugin(this);

        this.loadConfig();

        this.setCam(new CollisionAvoiderManager());
        this.setWf(new BCWandererManager());
        this.setIt(new IsTrainManager());

        Sponge.getEventManager().registerListener(this, new ByteCartListener());

        // register updater factory
        if (!this.getWandererManager().isWandererType("Updater")) {
            this.getWandererManager().register(new UpdaterFactory(), "Updater");
        }

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor(new BytecartCommandExecutor())
                .build(), "mego", "sendto", "bcreload", "bcupdater", "bcticket", "bcback");


        if (rootNode.getNode("hostname_resolution").getBoolean(true)) {
            hostnamePlugin = new BCHostnameResolutionPlugin();
            hostnamePlugin.onLoad();
            ByteCartAPI.setResolver(hostnamePlugin);
            Sponge.getEventManager().registerListener(this, hostnamePlugin);
            Sponge.getCommandManager().register(this, CommandSpec.builder()
                    .executor(hostnamePlugin)
                    .build(), "host");
        }

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
    protected final void loadConfig() {
        debug = rootNode.getNode("debug").getBoolean(false);
        keepitems = rootNode.getNode("keepitems").getBoolean(true);

        Lockduration = rootNode.getNode("Lockduration").getInt(44);

        if (debug) {
            log.info("ByteCartRedux : debug mode is on.");
        }

        if (rootNode.getNode("loadchunks").getBoolean()) {
            if (preloadchunklistener == null) {
                preloadchunklistener = new PreloadChunkListener();
                Sponge.getEventManager().registerListener(this, preloadchunklistener);
            }
        } else if (preloadchunklistener != null) {
            Sponge.getEventManager().unregisterListeners(preloadchunklistener);
            preloadchunklistener = null;
        }

        if (rootNode.getNode("constantspeedd").getBoolean(false)) {
            if (constantspeedlistener == null) {
                constantspeedlistener = new ConstantSpeedListener();
                Sponge.getEventManager().registerListener(this, constantspeedlistener);
            }
        } else if (constantspeedlistener != null) {
            Sponge.getEventManager().unregisterListeners(constantspeedlistener);
            constantspeedlistener = null;
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
        return keepitems;
    }

    /**
     * @return the resolver registered
     */
    public Resolver getResolver() {
        return resolver;
    }

    /**
     * Set the resolver that will be used
     *
     * @param resolver the resolver provided
     */
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
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
