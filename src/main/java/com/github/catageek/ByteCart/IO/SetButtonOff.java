package com.github.catageek.ByteCart.IO;

import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.Button;

import java.util.Map;

/**
 * this call represents a thread that powers off a button
 */
public class SetButtonOff implements Runnable {

    final private Component component;
    final private Map<Location, Integer> ActivatedButtonMap;

    /**
     * @param component the component to power off
     * @param ActivatedButtonMap a map containing the task id of current task
     */
    public SetButtonOff(Component component, Map<Location, Integer> ActivatedButtonMap) {
        this.component = component;
        this.ActivatedButtonMap = ActivatedButtonMap;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        BlockState block = component.getBlock().getState();

        if (block.getData() instanceof Button) {
            Button button = (Button) block.getData();

            button.setPowered(false);
            block.setData(button);

            block.update(false, true);
            MathUtil.forceUpdate(component.getBlock().getRelative(button.getAttachedFace()));
        }

        ActivatedButtonMap.remove(block.getLocation());
    }
}
