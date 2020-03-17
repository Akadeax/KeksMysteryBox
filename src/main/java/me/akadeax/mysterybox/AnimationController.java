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

public class AnimationController {
    public Reward goal;

    public ItemStack filler;

    public AnimationController(Reward goal) {
        this.goal = goal;
        filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }

    public void ShowAnimation(Player p) {
        Inventory animationInv = Bukkit.createInventory(null, 45, "Mystery Box");
        animationInv.addItem(goal.getDisplayItem());

        InventoryUtil.fillInventory(animationInv, filler);

        final int displayItemDelay = MysteryBox.mainConfig.getInt("rewardDisplay.displayItemDelay");
        final int displayItemAmount = MysteryBox.mainConfig.getInt("rewardDisplay.displayItemAmount");
        final int lastItemDisplayDelay = MysteryBox.mainConfig.getInt("rewardDisplay.lastItemDisplayDelay");
        new BukkitRunnable() {

            final int midSlot = 9 * 2 + 4;
            int itemsDisplayed = 0;

            Material last;

            @Override
            public void run() {
                p.openInventory(animationInv);

                if(itemsDisplayed < displayItemAmount) {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

                    ItemStack displayItem;
                    do {
                        displayItem = MathUtil.getRandomWeighted(MysteryBox.rewards).getDisplayItem();
                    } while(last == displayItem.getType());

                    animationInv.setItem(midSlot, displayItem);
                    last = displayItem.getType();

                    itemsDisplayed++;

                } else { // exec on last item
                    InventoryUtil.glassPaneFill(animationInv);
                    animationInv.setItem(midSlot, goal.getDisplayItem());
                    this.cancel();

                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

                    TimeUtil.runDelayed(() -> {
                        goal.giveReward(p);
                        p.closeInventory();
                    }, lastItemDisplayDelay, MysteryBox.getInstance());
                }
            }
        }.runTaskTimer(MysteryBox.getInstance(), 0, displayItemDelay);

        TimeUtil.runRepeat(() -> {
            p.openInventory(animationInv);
        }, 1,
           displayItemDelay * displayItemAmount + lastItemDisplayDelay,
            MysteryBox.getInstance());
    }
}
