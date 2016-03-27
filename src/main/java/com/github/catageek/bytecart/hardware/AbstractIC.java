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
import com.github.catageek.bytecart.io.ComponentSign;
import com.github.catageek.bytecart.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.Map;
import java.util.WeakHashMap;


// All ICs must inherit from this class

/**
 * An abstract class implementing common methods for all ICs
 */
abstract public class AbstractIC implements IC {

    private static final Map<String, Boolean> icCache = new WeakHashMap<String, Boolean>();
    private static org.bukkit.Location emptyLocation = new org.bukkit.Location(null, 0, 0, 0);
    final private Block Block;
    final private org.bukkit.Location Location;
    protected int Triggertax = 0;
    private RegistryInput[] input = new RegistryInput[9];
    private int input_args = 0;
    private RegistryOutput[] output = new RegistryOutput[6];
    private int output_args = 0;

    public AbstractIC(Block block) {
        this.Block = block;
        if (block != null) {
            this.Location = block.getLocation();
        } else {
            this.Location = new org.bukkit.Location(null, 0, 0, 0);
        }
    }

    static public final void removeFromCache(Block block) {
        icCache.remove(block.getLocation(emptyLocation).toString());
    }

    // This function checks if we have a ByteCartRedux sign at this location
    static public final boolean checkEligibility(Block b) {

        if (b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN) {
            return false;
        }

        Boolean ret;
        String s;
        if ((ret = icCache.get(s = b.getLocation(emptyLocation).toString())) != null) {
            return ret;
        }

        String line_content = ((Sign) b.getState()).getLine(1);

        if (ByteCartRedux.myPlugin.getConfig().getBoolean("FixBroken18", false)) {
            if (ret = AbstractIC.checkLooseEligibility(line_content)) {
                (new ComponentSign(b)).setLine(1, "[" + line_content + "]");
            } else {
                ret = AbstractIC.checkEligibility(line_content);
            }
        } else {
            ret = AbstractIC.checkEligibility(line_content);
        }
        icCache.put(s, ret);
        return ret;
    }

    static public final boolean checkEligibility(String s) {

        return s.matches("^\\[BC[0-9]{4,4}\\]$");

    }

    static public final boolean checkLooseEligibility(String s) {

        return s.matches("^BC[0-9]{4,4}$");

    }

    @Override
    abstract public String getName();

    @Override
    public String getFriendlyName() {
        return ((Sign) this.getBlock().getState()).getLine(2);
    }

    @Override
    public final void addInputRegistry(RegistryInput reg) {
        this.input[this.input_args++] = reg;
    }

    @Override
    public final void addOutputRegistry(RegistryOutput reg) {
        this.output[this.output_args++] = reg;
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
    public final BlockFace getCardinal() {
        try {
            BlockFace f = ((org.bukkit.material.Sign) this.getBlock().getState().getData()).getFacing().getOppositeFace();
            f = MathUtil.straightUp(f);
            if (f == BlockFace.UP) {
                ByteCartRedux.log.severe("ByteCartRedux: Tilted sign found at " + this.getLocation() + ". Please straight it up in the axis of the track");
            }
            return f;
        } catch (ClassCastException e) {
            // this is not a sign
            return null;
        }
    }

    @Override
    public final Block getBlock() {
        return Block;
    }

    @Override
    public final String getBuildPermission() {
        return "bytecart." + getName();
    }

    @Override
    public final int getTriggertax() {
        return ByteCartRedux.myPlugin.getConfig().getInt("usetax." + this.getName());
    }

    @Override
    public final int getBuildtax() {
        return ByteCartRedux.myPlugin.getConfig().getInt("buildtax." + this.getName());
    }

    @Override
    public org.bukkit.Location getLocation() {
        return Location;
    }
}
