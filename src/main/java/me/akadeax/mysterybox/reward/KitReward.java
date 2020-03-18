package me.akadeax.mysterybox.reward;

import com.google.gson.Gson;
import me.akadeax.mysterybox.MysteryBox;
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

public class KitReward extends Reward {

    public String kitName;
    public ItemStack[] reward;

    public KitReward(String kitName, ItemStack[] reward) {
        this.kitName = kitName;
        this.reward = reward;
    }

    @Override
    public void giveReward(Player toGiveTo) {
        toGiveTo.getInventory().addItem(reward);
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack display = new ItemStack(Material.CHEST);

        ItemMeta meta = display.getItemMeta();

        String nameDisplayFormat = MysteryBox.mainConfig.getString("rewardDisplay.kit");
        nameDisplayFormat = nameDisplayFormat.replace("{KITNAME}", kitName);
        meta.setDisplayName(nameDisplayFormat);

        display.setItemMeta(meta);

        return display;
    }


    public static List<Reward> loadRewardsFile(File rewardsFile, Gson gson) {
        try {

            if(rewardsFile.createNewFile()) {
                FileWriter fw = new FileWriter(rewardsFile);
                fw.write(gson.toJson(new KitReward[] {
                        new KitReward("Test", new ItemStack[] { new ItemStack(Material.DIAMOND_BLOCK) })
                }));
                fw.close();
            }

            String rewardsJson = new String(Files.readAllBytes(Paths.get(rewardsFile.getAbsolutePath())));
            return Arrays.asList(gson.fromJson(rewardsJson, KitReward[].class));

        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
