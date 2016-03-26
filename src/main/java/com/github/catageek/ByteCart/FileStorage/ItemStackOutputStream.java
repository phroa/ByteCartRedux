package com.github.catageek.ByteCart.FileStorage;

import org.bukkit.inventory.ItemStack;

import java.io.OutputStream;

/**
 * An output stream associated with an ItemStack
 */
abstract class ItemStackOutputStream extends OutputStream {

    private final ItemStack ItemStack;

    ItemStackOutputStream(org.bukkit.inventory.ItemStack itemStack) {
        super();
        ItemStack = itemStack;
    }

    final ItemStack getItemStack() {
        return ItemStack;
    }
}
