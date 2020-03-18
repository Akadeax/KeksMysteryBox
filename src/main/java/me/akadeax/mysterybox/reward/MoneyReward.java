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

public class MoneyReward extends Reward {

    public double amount;

    public MoneyReward(double reward) {
        this.amount = reward;
    }

    @Override
    public void giveReward(Player toGiveTo) {
        MysteryBox.getEconomy().depositPlayer(toGiveTo, amount);
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack display = new ItemStack(Material.PAPER);

        ItemMeta meta = display.getItemMeta();

        String nameDisplayFormat = MysteryBox.mainConfig.getString("rewardDisplay.money")
                .replace("{AMOUNT}", String.valueOf(amount));
        meta.setDisplayName(nameDisplayFormat);

        display.setItemMeta(meta);

        return display;
    }


    public static List<Reward> loadRewardsFile(File rewardsFile, Gson gson) {
        try {

            if(rewardsFile.createNewFile()) {
                FileWriter fw = new FileWriter(rewardsFile);
                fw.write(gson.toJson(new MoneyReward[] {
                        new MoneyReward(1)
                }));
                fw.close();
            }

            String rewardsJson = new String(Files.readAllBytes(Paths.get(rewardsFile.getAbsolutePath())));
            return Arrays.asList(gson.fromJson(rewardsJson, MoneyReward[].class));

        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
