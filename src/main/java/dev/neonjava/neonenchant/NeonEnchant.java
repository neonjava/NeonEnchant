package dev.neonjava.neonenchant;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class NeonEnchant extends JavaPlugin implements CommandExecutor {

    private static NeonEnchant instance;

    public static NeonEnchant get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (getCommand("customenchant") != null) {
            getCommand("customenchant").setExecutor(this);
        }
        getServer().getPluginManager().registerEvents(new EnchantMenuListener(), this);
        getLogger().info("NeonEnchant loaded successfully!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must be holding an item in your main hand to enchant it!", NamedTextColor.RED));
            return true;
        }

        EnchantMenu menu = new EnchantMenu(handItem);
        menu.open(player);
        return true;
    }
}
