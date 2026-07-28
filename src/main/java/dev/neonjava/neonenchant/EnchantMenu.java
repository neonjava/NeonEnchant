package dev.neonjava.neonenchant;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantMenu implements InventoryHolder {

    private final Inventory inventory;
    private final ItemStack baseItem;
    private final Set<EnchantmentData> availableEnchants = new LinkedHashSet<>();
    private final Set<EnchantmentData> selectedEnchants = new HashSet<>();

    public record EnchantmentData(Enchantment enchantment, int level, String displayName) {}

    public EnchantMenu(ItemStack baseItem) {
        this.inventory = Bukkit.createInventory(this, 36, Component.text("Enchant Item", NamedTextColor.DARK_GRAY));
        this.baseItem = baseItem.clone();

        // Populate available enchantments based on item type or standard set
        availableEnchants.add(new EnchantmentData(Enchantment.SHARPNESS, 5, "Sharpness V"));
        availableEnchants.add(new EnchantmentData(Enchantment.UNBREAKING, 3, "Unbreaking III"));
        availableEnchants.add(new EnchantmentData(Enchantment.FIRE_ASPECT, 2, "Fire Aspect II"));
        availableEnchants.add(new EnchantmentData(Enchantment.LOOTING, 3, "Looting III"));
        availableEnchants.add(new EnchantmentData(Enchantment.MENDING, 1, "Mending"));

        render();
    }

    public Set<EnchantmentData> getAvailableEnchants() {
        return availableEnchants;
    }

    public Set<EnchantmentData> getSelectedEnchants() {
        return selectedEnchants;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public void render() {
        inventory.clear();

        // 1. Fill background
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.displayName(Component.empty());
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, border);
        }

        // 2. Render Main Item in Slot 13 (Hover lore displays selected enchants)
        ItemStack itemToDisplay = baseItem.clone();
        ItemMeta itemMeta = itemToDisplay.getItemMeta();
        if (itemMeta != null) {
            List<Component> lore = new ArrayList<>();
            if (itemMeta.hasLore() && itemMeta.lore() != null) {
                lore.addAll(Objects.requireNonNull(itemMeta.lore()));
                lore.add(Component.empty());
            }
            lore.add(Component.text("Selected Enchantments:", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));

            if (selectedEnchants.isEmpty()) {
                lore.add(Component.text("  - None (Click books below)", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            } else {
                for (EnchantmentData data : selectedEnchants) {
                    lore.add(Component.text("  + " + data.displayName(), NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                }
            }
            itemMeta.lore(lore);
            itemToDisplay.setItemMeta(itemMeta);
        }
        inventory.setItem(13, itemToDisplay);

        // 3. Render Available Enchantment Books (Slots 19 to 25)
        int slot = 19;
        for (EnchantmentData data : availableEnchants) {
            boolean isSelected = selectedEnchants.contains(data);
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();

            if (bookMeta != null) {
                bookMeta.addStoredEnchant(data.enchantment(), data.level(), true);
                bookMeta.displayName(Component.text(data.displayName(), isSelected ? NamedTextColor.GREEN : NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));

                List<Component> bookLore = new ArrayList<>();
                if (isSelected) {
                    bookLore.add(Component.text("Status: ", NamedTextColor.GRAY).append(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false));
                    bookLore.add(Component.text("Click to remove this enchantment", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                    bookMeta.setEnchantmentGlintOverride(true);
                } else {
                    bookLore.add(Component.text("Status: ", NamedTextColor.GRAY).append(Component.text("NOT SELECTED", NamedTextColor.DARK_GRAY)).decoration(TextDecoration.ITALIC, false));
                    bookLore.add(Component.text("Click to select this enchantment", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                }
                bookMeta.lore(bookLore);
                book.setItemMeta(bookMeta);
            }
            inventory.setItem(slot++, book);
        }

        // 4. Render Confirm Button in Slot 31
        ItemStack confirm = new ItemStack(Material.ANVIL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.displayName(Component.text("Apply Enchantments", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            confirmMeta.lore(List.of(Component.text("Click to enchant item and take it!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            confirm.setItemMeta(confirmMeta);
        }
        inventory.setItem(31, confirm);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
