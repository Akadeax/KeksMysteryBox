package me.akadeax.mysterybox.menu;

import me.akadeax.mysterybox.MysteryBox;
import me.akadeax.mysterybox.reward.Reward;
import me.akadeax.mysterybox.util.InventoryUtil;
import me.akadeax.mysterybox.util.MathUtil;
import me.akadeax.mysterybox.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

// handles opening animation for Mystery Boxes
public class AnimationController implements Listener {

    private Player player;

    // the actual reward the player will get in the end
    public Reward goal;

    // item that fills outer slots around the random roll
    public ItemStack filler;

    private Inventory animationInv;

    // laod config vars
    final double displayItemDelayFactor = MysteryBox.mainConfig.getDouble("rewardDisplay.displayItemDelayFactor");
    final int displayItemAmount = MysteryBox.mainConfig.getInt("rewardDisplay.displayItemAmount");
    final int lastItemDisplayDelay = MysteryBox.mainConfig.getInt("rewardDisplay.lastItemDisplayDelay");

    final String mysteryBoxTitle = MysteryBox.mainConfig.getString("menu.mysteryBoxTitle");

    // middle slot of a 45 slot inv
    final int midSlot = 9 * 2 + 4;

    public AnimationController(Reward goal, Player toShowTo) {
        this.goal = goal;
        player = toShowTo;
        filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        MysteryBox.getPluginManager().registerEvents(this, MysteryBox.getInstance());
    }

    public void ShowAnimation() {
        animationInv = Bukkit.createInventory(null, 45, mysteryBoxTitle);

        InventoryUtil.fillInventory(animationInv, filler);

        player.closeInventory();
        player.openInventory(animationInv);

        final Listener instance = this;
        new BukkitRunnable() {
            // the mat that was randomly chosen last
            // (cuz avoid duplication of displays right after each other)
            Material last;

            int currentTicks = 0;
            int currentTicksBetweenRolls;
            int itemsDisplayed = 0;


            @Override
            public void run() {
                // keep track of how many ticks have passed so we can test against that
                currentTicks++;
                // quadratic function that determines the tick delay until the next display
                currentTicksBetweenRolls = (int) Math.round(displayItemDelayFactor * (itemsDisplayed * itemsDisplayed));

                // if enough ticks have passed
                if(currentTicks >= currentTicksBetweenRolls) {
                    itemsDisplayed++;
                    currentTicks = 0;
                    // display the next item (whether final or not)
                    if(itemsDisplayed < displayItemAmount) {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

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

                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        // fill rewarded item screen with random glass panes every few ticks (wow, confetti!)
                        TimeUtil.runRepeat(() -> {
                            InventoryUtil.randGlassPaneFill(animationInv, midSlot);
                        }, 5, lastItemDisplayDelay, MysteryBox.getInstance());

                        HandlerList.unregisterAll(instance);

                        TimeUtil.runDelayed(() -> {
                            goal.giveReward(player);
                            player.closeInventory();
                        }, lastItemDisplayDelay, MysteryBox.getInstance());
                    }
                }
            }

        }.runTaskTimer(MysteryBox.getInstance(), 0, 1);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if(title.equals(mysteryBoxTitle) && e.getView().getTopInventory() == e.getClickedInventory()) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(e.getPlayer().getUniqueId().equals(player.getUniqueId()) && e.getInventory().equals(animationInv)) {
            TimeUtil.runDelayed(() -> {
                player.openInventory(animationInv);
            }, 1, MysteryBox.getInstance());
        }
    }
}