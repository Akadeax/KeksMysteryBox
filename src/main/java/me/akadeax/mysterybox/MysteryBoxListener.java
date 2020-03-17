package me.akadeax.mysterybox;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MysteryBoxListener implements Listener {

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals("Mystery Box")) {
            e.setCancelled(true);
        }
    }

}
