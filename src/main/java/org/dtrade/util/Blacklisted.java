package org.dtrade.util;

import jdk.jshell.execution.Util;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dtrade.DTrade;
import org.dtrade.config.DTradeConfig;

import java.util.Arrays;

public class Blacklisted {

    public static boolean isItemBlacklisted(ItemStack item) {
        System.out.println(Arrays.toString(DTradeConfig.getBlacklistedMaterials()));
        if(item == null) return false;
        if(Utils.arrayContains(DTradeConfig.getBlacklistedMaterials(), item.getType())) return true;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;

        if(Utils.arrayContains(DTradeConfig.getBlacklistedNames(), meta.getDisplayName())) return true;

        if(meta.getLore() == null) return false;
        for(String line : meta.getLore()) if(Utils.arrayMatches(DTradeConfig.getBlacklistedLore(), line)) return true;
        return false;
    }

}