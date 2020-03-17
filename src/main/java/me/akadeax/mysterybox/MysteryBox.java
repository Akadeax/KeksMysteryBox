package me.akadeax.mysterybox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.akadeax.mysterybox.reward.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
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

    public static List<Reward> rewards = new ArrayList<>();

    public static FileConfiguration mainConfig;
    public static File rewardsFolder = null;

    public static File itemRewardsFile = null;
    public static File moneyRewardsFile = null;
    public static File permsRewardsFile = null;

    private static Economy econ = null;
    private static Permission perms = null;


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
        mainConfig.addDefault("rewardDisplay.displayItemAmount", 20);
        mainConfig.addDefault("rewardDisplay.displayItemDelay", 5);
        mainConfig.addDefault("rewardDisplay.lastItemDisplayDelay", 60);

        mainConfig.options().copyDefaults(true);
        saveConfig();

        Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();

        loadRewards(gson);
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new MysteryBoxListener(), this);
        getCommand("mysterybox").setExecutor(new MysteryBoxCommand());
    }

    @SuppressWarnings("ConstantConditions")
    private void loadRewards(Gson gson) {
        rewardsFolder = new File(getDataFolder().getAbsolutePath() + mainConfig.getString("rewardsFolder"));
        rewardsFolder.mkdir();

        itemRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/itemRewards.json");
        moneyRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/moneyRewards.json");
        permsRewardsFile = new File(rewardsFolder.getAbsolutePath() + "/permsRewards.json");

        rewards.addAll(ItemReward.loadRewardsFile(itemRewardsFile, gson));
        rewards.addAll(MoneyReward.loadRewardsFile(moneyRewardsFile, gson));
        rewards.addAll(PermissionReward.loadRewardsFile(permsRewardsFile, gson));
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
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if(rsp == null) return;
        perms = rsp.getProvider();
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }
}