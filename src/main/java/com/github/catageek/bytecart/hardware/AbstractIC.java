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
package com.github.catageek.bytecart.hardware;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


// All ICs must inherit from this class

/**
 * An abstract class implementing common methods for all ICs
 */
abstract public class AbstractIC implements IC {

    private static final Map<Location<World>, Boolean> IC_CACHE = new WeakHashMap<>();
    private static Location<World> location;
    private final BlockSnapshot block;
    private final RegistryInput[] input = new RegistryInput[9];
    private final RegistryOutput[] output = new RegistryOutput[6];
    private int inputArgs = 0;
    private int outputArgs = 0;

    protected AbstractIC(BlockSnapshot block) {
        this.block = block;
        location = block.getLocation().orElse(new Location<>(Sponge.getServer().getWorlds().toArray(new World[0])[0], 0, 0, 0));
    }

    public static void removeFromCache(BlockSnapshot block) {
        IC_CACHE.remove(location = block.getLocation().get());
    }

    // This function checks if we have a ByteCartRedux sign at this location
    public static boolean checkEligibility(BlockSnapshot b) {

        if (!b.supports(Keys.SIGN_LINES)) {
            return false;
        }

        if (IC_CACHE.containsKey(location = b.getLocation().get())) {
            return true;
        }

        boolean ret;

        String secondLine = b.get(Keys.SIGN_LINES).get().get(1).toPlain();

        if (ByteCartRedux.rootNode.getNode("FixBroken18").getBoolean(false)) {
            if (ret = AbstractIC.checkLooseEligibility(secondLine)) {
                List<Text> list = b.get(Keys.SIGN_LINES).get();
                list.set(1, Text.of("[", secondLine, "]"));
                b.with(Keys.SIGN_LINES, list);
            } else {
                ret = AbstractIC.checkEligibility(secondLine);
            }
        } else {
            ret = AbstractIC.checkEligibility(secondLine);
        }
        IC_CACHE.put(location, ret);
        return ret;
    }

    public static boolean checkEligibility(String s) {

        return s.matches("^\\[BC[0-9]{4}\\]$");

    }

    private static boolean checkLooseEligibility(String s) {

        return s.matches("^BC[0-9]{4}$");

    }

    @Override
    abstract public String getName();

    @Override
    public String getFriendlyName() {
        return block.get(Keys.SIGN_LINES).get().get(2).toPlain();
    }

    @Override
    public final void addInputRegistry(RegistryInput reg) {
        this.input[this.inputArgs++] = reg;
    }

    @Override
    public final void addOutputRegistry(RegistryOutput reg) {
        this.output[this.outputArgs++] = reg;
    }

    @Override
    public final RegistryInput getInput(int index) {
        return input[index];
    }

    @Override
    public final RegistryOutput getOutput(int index) {
        return output[index];
    }

    @Override
    public final Direction getCardinal() {
        return MathUtil.straightUp(block.get(Keys.DIRECTION).get().getOpposite());
    }

    @Override
    public final BlockSnapshot getBlock() {
        return block;
    }

    @Override
    public final String getBuildPermission() {
        return "bytecart." + getName();
    }

    @Override
    public Location<World> getLocation() {
        return block.getLocation().get();
    }
}
