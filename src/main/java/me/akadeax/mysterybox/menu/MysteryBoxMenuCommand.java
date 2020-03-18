package me.akadeax.mysterybox.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MysteryBoxMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = (Player)sender;
        MysteryBoxMenu menu = new MysteryBoxMenu(p);
        menu.openInventory();

        return true;
    }


}
