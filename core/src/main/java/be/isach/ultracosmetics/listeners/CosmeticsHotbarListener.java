package be.isach.ultracosmetics.listeners;

import org.bukkit.ChatColor;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.menu.menus.MenuMain;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CosmeticsHotbarListener implements Listener {

    private final UltraCosmetics ultraCosmetics;
    private static final String MENU_ITEM_NAME = ChatColor.AQUA + "" + ChatColor.BOLD + "ᴄᴏꜱᴍᴇᴛɪᴄꜱ " + ChatColor.DARK_GRAY + " " + ChatColor.GRAY + "ʀɪɢʜᴛ ᴄʟɪᴄᴋ";

    public CosmeticsHotbarListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        ultraCosmetics.getSmartLogger().write("CosmeticsHotbarListener initialized!");
    }

    private ItemStack createMenuItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MENU_ITEM_NAME);
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isMenuItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && MENU_ITEM_NAME.equals(meta.getDisplayName());
    }

    private void giveMenuItem(Player player) {
        ultraCosmetics.getSmartLogger().write("Giving menu item to " + player.getName());
        // Remove any existing menu items first
        for (ItemStack item : player.getInventory().getContents()) {
            if (isMenuItem(item)) {
                player.getInventory().remove(item);
            }
        }
        // Set the item in slot 0
        player.getInventory().setItem(0, createMenuItem());
        ultraCosmetics.getSmartLogger().write("Menu item given to " + player.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ultraCosmetics.getSmartLogger().write("Player joined: " + player.getName());
        // Use delayed task to ensure inventory is ready
        ultraCosmetics.getScheduler().runAtEntityLater(player, task -> giveMenuItem(player), null, 10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ultraCosmetics.getSmartLogger().write("Player respawned: " + player.getName());
        // Use Folia scheduler
        ultraCosmetics.getScheduler().runAtEntityLater(player, task -> giveMenuItem(player), null, 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isMenuItem(item) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            ultraCosmetics.getSmartLogger().write("Opening menu for " + player.getName());
            UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
            new MenuMain(ultraCosmetics).open(ultraPlayer);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            ultraCosmetics.getSmartLogger().write("Prevented " + event.getPlayer().getName() + " from dropping menu item");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Prevent moving the menu item
        if (isMenuItem(clickedItem) || isMenuItem(cursorItem)) {
            // Only allow clicking in cosmetics menu, block everywhere else
            if (!(event.getInventory().getHolder() instanceof be.isach.ultracosmetics.menu.CosmeticsInventoryHolder)) {
                event.setCancelled(true);
            }
        }

        // Prevent moving items to slot 0
        if (event.getSlot() == 0 && event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            if (!isMenuItem(cursorItem) && cursorItem != null && cursorItem.getType() != Material.AIR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        // Prevent dragging the menu item
        if (isMenuItem(event.getOldCursor())) {
            event.setCancelled(true);
        }
        
        // Prevent dragging to slot 0
        if (event.getRawSlots().contains(0)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (isMenuItem(event.getMainHandItem()) || isMenuItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Remove menu item from drops
        event.getDrops().removeIf(this::isMenuItem);
    }
}