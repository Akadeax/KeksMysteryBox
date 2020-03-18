package me.akadeax.mysterybox.reward;

import com.google.gson.Gson;
import me.akadeax.mysterybox.MysteryBox;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
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
import java.util.concurrent.TimeUnit;

public class RankReward extends Reward {

    public String rank;
    public long daysDuration;

    public RankReward(String rank, long duration) {
        this.rank = rank;
        this.daysDuration = duration;
    }

    @Override
    public void giveReward(Player toGiveTo) {
        User user = MysteryBox.getPermissions().getUserManager().getUser(toGiveTo.getUniqueId());
        NodeBuilder builder = Node.builder("group." + rank);
        if(daysDuration > 0) {
            builder = builder.expiry(daysDuration, TimeUnit.DAYS);
        }
        user.data().add(builder.build());
        MysteryBox.getPermissions().getUserManager().saveUser(user);
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack display = new ItemStack(Material.IRON_CHESTPLATE);

        ItemMeta meta = display.getItemMeta();

        String nameDisplayFormat = MysteryBox.mainConfig.getString("rewardDisplay.rank");
        nameDisplayFormat = nameDisplayFormat.replace("{RANK}", rank);
        meta.setDisplayName(nameDisplayFormat);

        if(daysDuration > 0) {
            String durationDisplayFormat = MysteryBox.mainConfig.getString("rewardDisplay.rankDuration");
            durationDisplayFormat = durationDisplayFormat.replace("{DURATION}", String.valueOf(daysDuration));
            meta.setLore(Arrays.asList(durationDisplayFormat));
        }

        display.setItemMeta(meta);

        return display;
    }

    public static List<Reward> loadRewardsFile(File rewardsFile, Gson gson) {
        try {

            if(rewardsFile.createNewFile()) {
                FileWriter fw = new FileWriter(rewardsFile);
                fw.write(gson.toJson(new RankReward[]{
                        new RankReward("admin", 100)
                }));
                fw.close();
            }

            String rewardsJson = new String(Files.readAllBytes(Paths.get(rewardsFile.getAbsolutePath())));
            return Arrays.asList(gson.fromJson(rewardsJson, RankReward[].class));

        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
