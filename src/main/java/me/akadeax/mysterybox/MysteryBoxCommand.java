package me.akadeax.mysterybox;

import me.akadeax.mysterybox.reward.Reward;
import me.akadeax.mysterybox.util.MathUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MysteryBoxCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
    
        Player p = (Player)sender;

        Reward goal = MathUtil.getRandomWeighted(MysteryBox.rewards);
        AnimationController anim = new AnimationController(goal);
        anim.ShowAnimation(p);

        return true;
    }
    
    
}
