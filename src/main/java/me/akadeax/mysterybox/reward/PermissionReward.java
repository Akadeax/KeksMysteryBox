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

public class PermissionReward extends Reward {

    public String permission;

    public PermissionReward(String permission) {
        this.permission = permission;
        this.rewardType = RewardType.Permission;
    }


    @Override
    public void giveReward(Player toGiveTo) {
        toGiveTo.sendMessage("§aYa got permission " + permission);
        MysteryBox.getPermissions().playerAdd(toGiveTo, permission);
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack display = new ItemStack(Material.COMMAND_BLOCK);

        ItemMeta meta = display.getItemMeta();

        String nameDisplayFormat = MysteryBox.mainConfig.getString("rewardDisplay.permission");
        nameDisplayFormat = nameDisplayFormat.replace("{PERMISSION}", permission);
        meta.setDisplayName(nameDisplayFormat);

        display.setItemMeta(meta);

        return display;
    }


    public static List<Reward> loadRewardsFile(File rewardsFile, Gson gson) {
        try {

            if(rewardsFile.createNewFile()) {
                FileWriter fw = new FileWriter(rewardsFile);
                fw.write(gson.toJson(new PermissionReward[]{
                        new PermissionReward("essentials.*")
                }));
                fw.close();
            }

            String rewardsJson = new String(Files.readAllBytes(Paths.get(rewardsFile.getAbsolutePath())));
            return Arrays.asList(gson.fromJson(rewardsJson, PermissionReward[].class));

        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
