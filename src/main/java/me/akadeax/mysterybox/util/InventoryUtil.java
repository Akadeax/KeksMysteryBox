package me.akadeax.mysterybox.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static void fillInventory(Inventory inv, ItemStack fillWith) {
        for(int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, fillWith);
        }
    }

    public static void glassPaneFill(Inventory inv) {
        for(int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemStack(MaterialUtil.rainbowStainedGlassPane()));
        }
    }
}
