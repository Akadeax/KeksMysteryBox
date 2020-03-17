package me.akadeax.mysterybox.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeUtil {
    public static void runRepeat(Runnable toRun, int repeatDelay, int repeatFor, Plugin p) {
        new BukkitRunnable() {
            final int repeatsAmount = repeatFor / repeatDelay;
            int currentRepeatAmount = 0;
            @Override
            public void run() {
                currentRepeatAmount++;
                if(currentRepeatAmount > repeatsAmount) {
                    this.cancel();
                }
                toRun.run();
            }
        }.runTaskTimer(p, 0, repeatDelay);
    }

    public static void runDelayed(Runnable r, int delay, Plugin p) {
        new BukkitRunnable() {

            @Override
            public void run() {
                r.run();
            }
        }.runTaskLater(p, delay);
    }
}
