package me.akadeax.mysterybox.menu;

import me.akadeax.mysterybox.MysteryBox;
import me.akadeax.mysterybox.reward.Reward;
import me.akadeax.mysterybox.util.InventoryUtil;
import me.akadeax.mysterybox.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MysteryBoxMenu implements Listener {

    private final String mysteryBoxMenuTitle;
    private final int keyModelData;

    private Player toOpenFor;

    public ItemStack filler;
    public ItemStack emptyKeySlot;

    public MysteryBoxMenu(Player toOpenFor) {
        // register this plugins events (handle events in every instance)
        MysteryBox.getPluginManager().registerEvents(this, MysteryBox.getInstance());
        // load config stuff now so we don't have to load over and over later
        mysteryBoxMenuTitle = MysteryBox.mainConfig.getString("menu.mysteryBoxMenuTitle");
        keyModelData = MysteryBox.mainConfig.getInt("key.keyModelData");

        this.toOpenFor = toOpenFor;
        filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        emptyKeySlot = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta emptyKeySlotMeta = emptyKeySlot.getItemMeta();

        emptyKeySlotMeta.setDisplayName(MysteryBox.mainConfig.getString("menu.keySlotDisplayName"));
        emptyKeySlot.setItemMeta(emptyKeySlotMeta);
    }


    public void openInventory() {
        Inventory menuInv = Bukkit.createInventory(null, 18, mysteryBoxMenuTitle);
        toOpenFor.openInventory(menuInv);
        InventoryUtil.fillInventory(menuInv, filler);
        menuInv.setItem(4, emptyKeySlot);
    }


    private void openMysteryBox(Player p) {
        Reward goal = MathUtil.getRandomWeighted(MysteryBox.rewards);
        AnimationController anim = new AnimationController(goal, toOpenFor);
        anim.ShowAnimation();
    }


    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if(!e.getWhoClicked().getUniqueId().equals(toOpenFor.getUniqueId())) return;

        String title = e.getView().getTitle();
        // if menu inv is clicked
        if(title.equals(mysteryBoxMenuTitle) && e.getView().getTopInventory() == e.getClickedInventory()) {
            // if empty key slot item is clicked
            if(e.getCurrentItem() != null && e.getCurrentItem().getType().equals(emptyKeySlot.getType())) {
                // nullcheck
                if(!e.getCursor().hasItemMeta() || !e.getCursor().getItemMeta().hasCustomModelData()) {
                    e.setCancelled(true);
                    return;
                }
                // keys are marked with model data
                int modelData = e.getCursor().getItemMeta().getCustomModelData();
                if(modelData == keyModelData) {
                    ItemStack cursor = e.getWhoClicked().getItemOnCursor();
                    // if user clicked with more than 1 key, give em back the rest
                    if(cursor.getAmount() > 1) {
                        cursor.setAmount(cursor.getAmount() - 1);
                        e.getWhoClicked().getInventory().addItem(cursor);
                    }
                    e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

                    HandlerList.unregisterAll(this);
                    openMysteryBox(toOpenFor);
                }
            }
            e.setCancelled(true);
        }
    }

}