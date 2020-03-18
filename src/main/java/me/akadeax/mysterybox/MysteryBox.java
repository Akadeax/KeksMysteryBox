package me.akadeax.mysterybox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.akadeax.mysterybox.menu.MysteryBoxMenuCommand;
import me.akadeax.mysterybox.reward.*;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class MysteryBox extends JavaPlugin {

    private static MysteryBox instance;
    public static MysteryBox getInstance() {
        return instance;
    }

    public static PluginManager getPluginManager() {
        return instance.getServer().getPluginManager();
    }

    public static List<Reward> rewards = new ArrayList<>();

    public static FileConfiguration mainConfig;
    public static File rewardsFolder = null;

    public static File itemRewardsFile = null;
    public static File moneyRewardsFile = null;
    public static File permRewardsFile = null;
    public static File rankRewardsFile = null;
    public static File kitRewardsFile = null;

    private static Economy econ = null;
    private static LuckPerms perms = null;


    @Override
    public void onEnable() {
        instance = this;
        mainConfig = getConfig();

        if (!setupEconomy() ) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();

        register();

        mainConfig.addDefault("rewardsFolder", "/rewards/");

        mainConfig.addDefault("rewardDisplay.money", "§bMoney Reward of §6{AMOUNT}$");
        mainConfig.addDefault("rewardDisplay.permission", "§bPermission §6{PERMISSION}");
        mainConfig.addDefault("rewardDisplay.rank", "§bRank §6{RANK}");
        mainConfig.addDefault("rewardDisplay.rankDuration", "§7For {DURATION} days");
        mainConfig.addDefault("rewardDisplay.kit", "§bKit §6{KITNAME}");
        mainConfig.addDefault("rewardDisplay.displayItemAmount", 20);
        mainConfig.addDefault("rewardDisplay.displayItemDelayFactor", 0.2d);
        mainConfig.addDefault("rewardDisplay.lastItemDisplayDelay", 60);

        mainConfig.addDefault("menu.keySlotDisplayName", "§cInsert key to open");
        mainConfig.addDefault("menu.mysteryBoxTitle", "Mystery Box");
        mainConfig.addDefault("menu.mysteryBoxMenuTitle", "Mystery Box Menu");
        mainConfig.addDefault("menu.keySlotDisplayName", "§cInsert key to open");

        mainConfig.addDefault("key.keyModelData", 1000);


        mainConfig.options().copyDefaults(true);
        saveConfig();

        Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();

        loadRewards(gson);
    }

    @SuppressWarnings("ConstantConditions")
    private void register() {
        getCommand("mysterybox").setExecutor(new MysteryBoxMenuCommand());
    }

    @SuppressWarnings("ConstantConditions")
    private void loadRewards(Gson gson) {
        rewardsFolder = new File(getDataFolder().getAbsolutePath() + mainConfig.getString("rewardsFolder"));
        rewardsFolder.mkdir();

        itemRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/itemRewards.json");
        moneyRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/moneyRewards.json");
        permRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/permRewards.json");
        rankRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/rankRewards.json");
        kitRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/kitRewards.json");

        rewards.addAll(ItemReward.loadRewardsFile(itemRewardsFile, gson));
        rewards.addAll(MoneyReward.loadRewardsFile(moneyRewardsFile, gson));
        rewards.addAll(PermissionReward.loadRewardsFile(permRewardsFile, gson));
        rewards.addAll(RankReward.loadRewardsFile(rankRewardsFile, gson));
        rewards.addAll(KitReward.loadRewardsFile(kitRewardsFile, gson));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void setupPermissions() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            perms = provider.getProvider();
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static LuckPerms getPermissions() {
        return perms;
    }
}