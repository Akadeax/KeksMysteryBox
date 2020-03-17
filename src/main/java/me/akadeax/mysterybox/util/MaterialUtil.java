package me.akadeax.mysterybox.util;

import org.bukkit.Material;

import java.util.Random;

public class MaterialUtil {
    public static Material rainbowStainedGlassPane() {
        Material[] panes = new Material[] {
                Material.WHITE_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE
        };

        return panes[new Random().nextInt(panes.length)];
    }
}
