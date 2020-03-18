package me.akadeax.mysterybox.reward;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ItemReward extends Reward {

    public ItemStack reward;

    public ItemReward(ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void giveReward(Player toGiveTo) {
        toGiveTo.getInventory().addItem(reward);
    }

    @Override
    public ItemStack getDisplayItem() {
        return reward;
    }


    public static List<Reward> loadRewardsFile(File rewardsFile, Gson gson) {
        try {

            if(rewardsFile.createNewFile()) {
                FileWriter fw = new FileWriter(rewardsFile);
                fw.write(gson.toJson(new ItemReward[] {
                        new ItemReward(new ItemStack(Material.STONE))
                }));
                fw.close();
            }

            String rewardsJson = new String(Files.readAllBytes(Paths.get(rewardsFile.getAbsolutePath())));
            return Arrays.asList(gson.fromJson(rewardsJson, ItemReward[].class));

        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
