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
import com.github.catageek.bytecart.address.ReturnAddressFactory;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collision.CollisionAvoiderBuilder;
import com.github.catageek.bytecart.collision.Router;
import com.github.catageek.bytecart.collision.RouterCollisionAvoiderBuilder;
import com.github.catageek.bytecart.event.custom.SignPostRouteEvent;
import com.github.catageek.bytecart.event.custom.SignPreRouteEvent;
import com.github.catageek.bytecart.event.custom.UpdaterPassRouterEvent;
import com.github.catageek.bytecart.routing.RoutingTableFactory;
import com.github.catageek.bytecart.routing.RoutingTableWritable;
import com.github.catageek.bytecart.util.DirectionRegistry;
import com.github.catageek.bytecart.util.MathUtil;
import com.github.catageek.bytecart.updater.AbstractWanderer;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.io.IOException;


/**
 * An IC at the entry of a L1 router
 */
public class BC8010 extends AbstractTriggeredSign implements BCRouter, Triggerable, HasRoutingTable {

    private final BlockFace From;
    private final Address Sign;
    private final RoutingTableWritable RoutingTable;
    private final Block center;
    protected boolean IsTrackNumberProvider;
    private AddressRouted destination;

    BC8010(Block block, org.bukkit.entity.Vehicle vehicle) throws ClassNotFoundException, IOException {
        super(block, vehicle);
        this.IsTrackNumberProvider = true;
        From = this.getCardinal().getOppositeFace();
        // reading destination address of the cart
        destination = AddressFactory.getAddress(this.getInventory());
        if (destination == null) {
            destination = AddressFactory.getDefaultTicket(this.getInventory());
        }
        // reading address written on BC8010 sign
        Sign = AddressFactory.getAddress(this.getBlock(), 3);
        // Center of the router, at sign level
        center = this.getBlock().getRelative(this.getCardinal(), 6).getRelative(MathUtil.clockwise(this.getCardinal()));

        BlockState blockstate;

        if ((blockstate = center.getRelative(BlockFace.UP, 5).getState()) instanceof InventoryHolder) {
            // Loading inventory of chest above router
            Inventory ChestInventory = ((InventoryHolder) blockstate).getInventory();

            // Converting inventory in routing table
            RoutingTable = RoutingTableFactory.getRoutingTable(ChestInventory);
        } else {
            RoutingTable = null;
        }
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {

        CollisionAvoiderBuilder builder = new RouterCollisionAvoiderBuilder(this, center.getLocation());

        try {

            BlockFace direction, to;
            Router router = ByteCartRedux.myPlugin.getCollisionAvoiderManager().getCollisionAvoider(builder);
            boolean isTrain = AbstractTriggeredSign.isTrain(destination);

            // Here begins the triggered action

            // is this an wanderer who needs special routing ? no then routing normally
            if (selectWanderer()) {

                // if this is a cart in a train
                if (this.wasTrain(this.getLocation())) {

                    // leave a message to next cart that it is a train
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getLocation());
                    // tell to router not to change position
                    ByteCartRedux.myPlugin.getCollisionAvoiderManager().<Router>getCollisionAvoider(builder).Book(isTrain);
                    return;
                }

                if (destination != null) {
                    // Time-to-live management

                    //loading TTl of cart
                    int ttl = destination.getTTL();

                    // if ttl did not reach end of life ( = 0)
                    if (ttl != 0) {

                        destination.updateTTL(ttl - 1);
                    }

                    // if ttl was 1 (now 0), we try to return the cart to source station

                    if (ttl == 1 && tryReturnCart()) {
                        destination = AddressFactory.getAddress(this.getInventory());
                    }

                    if (ByteCartRedux.debug) {
                        ByteCartRedux.log.info("ByteCartRedux : TTL is " + destination.getTTL());
                    }


                    // if this is the first car of a train
                    // we keep it during 2 s
                    if (isTrain) {
                        this.setWasTrain(this.getLocation(), true);
                    }

                    destination.finalizeAddress();
                }

                direction = this.SelectRoute(destination, Sign, RoutingTable);

                // trigger event
                BlockFace bdest = router.WishToGo(From, direction, isTrain);
                int ring = this.getRoutingTable().getDirectlyConnected(new DirectionRegistry(bdest));
                SignPostRouteEvent event = new SignPostRouteEvent(this, ring);
                Bukkit.getServer().getPluginManager().callEvent(event);

                return;
            }

            // it's a wanderer, so let it choosing direction
            Wanderer wanderer = getWanderer();

            // routing normally
            to = router.WishToGo(From, wanderer.giveRouterDirection(), isTrain);

            if (WandererContentFactory.isWanderer(getInventory(), "Updater")) {
                int nextring = this.getRoutingTable().getDirectlyConnected(new DirectionRegistry(to));
                UpdaterPassRouterEvent event = new UpdaterPassRouterEvent(wanderer, to, nextring);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }

            // here we perform routes update
            wanderer.doAction(to);

        } catch (ClassCastException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }
            e.printStackTrace();

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

    /**
     * Tells if this cart needs normal routing
     * @return true if the cart needs normal routing
     */
    protected boolean selectWanderer() {
        // everything that is not an wanderer must be routed
        return !WandererContentFactory.isWanderer(getInventory());
    }

    /**
     * Compute the direction to take
     *
     * @param IPaddress the destination address
     * @param sign the BC sign
     * @param RoutingTableWritable the routing table contained in the chest
     * @return the direction to destination, or to ring 0. If ring 0 does not exist, random direction
     */
    protected BlockFace SelectRoute(AddressRouted IPaddress, Address sign, RoutingTableWritable RoutingTable) {

        DirectionRegistry face;
        // same region : lookup destination track
        if (IPaddress != null && IPaddress.getRegion().getAmount() == sign.getRegion().getAmount() && IPaddress.getTTL() != 0) {
            int destination = this.destination.getTrack().getAmount();
            DirectionRegistry out = RoutingTable.getDirection(destination);
            if (out != null) {
                // trigger event
                SignPreRouteEvent event = new SignPreRouteEvent(this, this.getRoutingTable().getDirectlyConnected(out));
                Bukkit.getServer().getPluginManager().callEvent(event);
                return RoutingTable.getDirection(event.getTargetTrack()).getBlockFace();
            }
        }

        // If not in same region, or if TTL is 0, or the ring does not exist then we lookup track 0
        if ((face = RoutingTable.getDirection(0)) != null) {
            return face.getBlockFace();
        }

        // If everything has failed, then we randomize output direction
        return AbstractWanderer.getRandomBlockFace(RoutingTable, getCardinal().getOppositeFace());
    }

    /**
     * Try to send the cart to its return address
     *
     * @return true if success
     */
    private boolean tryReturnCart() {
        Address returnAddress = ReturnAddressFactory.getAddress(this.getInventory());
        if (returnAddress != null && returnAddress.isReturnable()) {
            (new BC7017(this.getBlock(), this.getVehicle())).trigger();
            return true;
        }
        return false;
    }

    /**
     * Get the wanderer object
     *
     * @return the wanderer
     * @throws ClassNotFoundException
     * @throws IOException
     */
    protected final Wanderer getWanderer() throws ClassNotFoundException, IOException {
        return ByteCartRedux.myPlugin.getWandererManager().getFactory(this.getInventory()).getWanderer(this, this.getInventory());
    }


    @Override
    public Wanderer.Level getLevel() {
        return Wanderer.Level.REGION;
    }

    /**
     * Return the direction from where the cart is coming
     *
     * @return the direction
     */
    @Override
    public final BlockFace getFrom() {
        return From;
    }

    @Override
    public final Address getSignAddress() {
        return Sign;
    }

    @Override
    public final RoutingTableWritable getRoutingTable() {
        return RoutingTable;
    }

    /**
     * Tell if this IC will provide track numbers during configuration
     *
     * @return true if the IC provides track number
     */
    public final boolean isTrackNumberProvider() {
        return IsTrackNumberProvider;
    }

    @Override
    public final String getDestinationIP() {
        return destination.toString();
    }

    @Override
    public final int getOriginTrack() {
        return Sign.getTrack().getAmount();
    }

    @Override
    public final Block getCenter() {
        return center;
    }

    @Override
    public String getName() {
        return "BC8010";
    }

    @Override
    public String getFriendlyName() {
        return "L1 Router";
    }
}