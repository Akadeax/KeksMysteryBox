package me.akadeax.mysterybox.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Reward {
    public int weight = 1;
    public RewardType rewardType;
    public abstract void giveReward(Player toGiveTo);

    public abstract ItemStack getDisplayItem();

    public Reward() {

    }

    public enum RewardType {
        Item, Money, Permission
    }
}
