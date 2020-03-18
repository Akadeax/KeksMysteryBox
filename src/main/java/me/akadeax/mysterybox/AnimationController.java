package me.akadeax.mysterybox;

import me.akadeax.mysterybox.reward.Reward;
import me.akadeax.mysterybox.util.InventoryUtil;
import me.akadeax.mysterybox.util.MathUtil;
import me.akadeax.mysterybox.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

// handles opening animation for Mystery Boxes
public class AnimationController {
    // the actual reward the player will get in the end
    public Reward goal;

    // item that fills outer slots around the random roll
    public ItemStack filler;

    private Inventory animationInv;

    // laod config vars
    final double displayItemDelayFactor = MysteryBox.mainConfig.getDouble("rewardDisplay.displayItemDelayFactor");
    final int displayItemAmount = MysteryBox.mainConfig.getInt("rewardDisplay.displayItemAmount");
    final int lastItemDisplayDelay = MysteryBox.mainConfig.getInt("rewardDisplay.lastItemDisplayDelay");

    // middle slot of a 45 slot inv
    final int midSlot = 9 * 2 + 4;

    public AnimationController(Reward goal) {
        this.goal = goal;
        filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }

    public void ShowAnimation(Player p) {
        animationInv = Bukkit.createInventory(null, 45, "Mystery Box");

        InventoryUtil.fillInventory(animationInv, filler);

        new BukkitRunnable() {
            // the mat that was randomly chosen last
            // (cuz avoid duplication of displays right after each other)
            Material last;

            int currentTicks = 0;
            int currentTicksBetweenRolls;
            int itemsDisplayed = 0;

            @Override
            public void run() {
                // force the inventory to stay open on the player
                p.openInventory(animationInv);
                // keep track of how many ticks have passed so we can test against that
                currentTicks++;
                // quadratic function that determines the delay between displays
                currentTicksBetweenRolls = (int) Math.round(displayItemDelayFactor * (itemsDisplayed * itemsDisplayed));

                Bukkit.broadcastMessage(itemsDisplayed + " " + currentTicksBetweenRolls);

                // if enough ticks have passed
                if(currentTicks >= currentTicksBetweenRolls) {
                    itemsDisplayed++;
                    currentTicks = 0;
                    // display the next item (whether final or not)
                    if(itemsDisplayed < displayItemAmount) {
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                        ItemStack displayItem;
                        do {
                            displayItem = MathUtil.getRandomWeighted(MysteryBox.rewards).getDisplayItem();
                        } while(last == displayItem.getType());

                        animationInv.setItem(midSlot, displayItem);
                        last = displayItem.getType();
                    } else { // exec on last item
                        // set the actual reward as the last item that rolls
                        animationInv.setItem(midSlot, goal.getDisplayItem());
                        this.cancel();

                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        // fill rewarded item screen with random glass panes every few ticks (wow, confetti!)
                        TimeUtil.runRepeat(() -> {
                            InventoryUtil.randGlassPaneFill(animationInv, midSlot);
                        }, 5, lastItemDisplayDelay, MysteryBox.getInstance());

                        TimeUtil.runDelayed(() -> {
                            goal.giveReward(p);
                            p.closeInventory();
                        }, lastItemDisplayDelay, MysteryBox.getInstance());
                    }
                }
            }

        }.runTaskTimer(MysteryBox.getInstance(), 0, 1);
    }
}

