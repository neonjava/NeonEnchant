package dev.neonjava.neonenchant;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EnchantMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantMenu menu)) return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();

        // 1. Clicked an Enchantment Book (Slots 19 to 25)
        if (slot >= 19 && slot < 19 + menu.getAvailableEnchants().size()) {
            int index = slot - 19;
            EnchantMenu.EnchantmentData clickedData = new ArrayList<>(menu.getAvailableEnchants()).get(index);

            // Toggle selection
            if (menu.getSelectedEnchants().contains(clickedData)) {
                menu.getSelectedEnchants().remove(clickedData);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
            } else {
                menu.getSelectedEnchants().add(clickedData);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
            }

            // Re-render menu so hover lore and book glint update
            menu.render();
            return;
        }

        // 2. Clicked Confirm Button (Slot 31)
        if (slot == 31) {
            if (menu.getSelectedEnchants().isEmpty()) {
                player.sendMessage(Component.text("You haven't selected any enchantments!", NamedTextColor.RED));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            // Apply selected enchantments to player's held item
            ItemStack result = menu.getBaseItem().clone();
            ItemMeta meta = result.getItemMeta();
            if (meta != null) {
                for (EnchantMenu.EnchantmentData data : menu.getSelectedEnchants()) {
                    meta.addEnchant(data.enchantment(), data.level(), true);
                }
                result.setItemMeta(meta);
            }

            // Update item in player hand
            player.getInventory().setItemInMainHand(result);
            player.closeInventory();
            player.sendMessage(Component.text("Item successfully enchanted!", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
        }
    }
}
