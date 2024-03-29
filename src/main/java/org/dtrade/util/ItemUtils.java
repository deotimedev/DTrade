package org.dtrade.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public final class ItemUtils {

    public static ItemStack setDisplayName(ItemStack item, String displayName) {
        if(item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addLore(ItemStack item, String... added) {
        if(item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.addAll(Arrays.asList(added));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack clearLore(ItemStack item) {
        if(item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addGlint(ItemStack item) {
        if(item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public static String getRealName(ItemStack item) {
        if (item.getItemMeta() != null) {
            if (item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();
        }
        return WordUtils.capitalize(item.getType().toString().replace('_', ' ').toLowerCase());
    }

    public static ItemStack createMenuGlass() {
        return setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "");
    }

    public static ItemStack createGui(Material material, String name) {
        return setDisplayName(new ItemStack(material), name);
    }

    public static void addToInventoryOrDrop(Plugin plugin, Player player, ItemStack[] items, Consumer<ItemStack> onDrop) {
        for(ItemStack item : items) {
            if(player.getInventory().firstEmpty() != -1) player.getInventory().addItem(item);
            else {
                player.getWorld().dropItemNaturally(player.getLocation(), item, (i) -> {
                    i.setOwner(player.getUniqueId());
                    for(Player p : Bukkit.getOnlinePlayers()) if(!p.equals(player)) p.hideEntity(plugin, i);
                });
                if(onDrop != null) onDrop.accept(item);
            }
        }
    }

}
