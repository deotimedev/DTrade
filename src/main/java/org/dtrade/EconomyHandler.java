package org.dtrade;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {

    private final Plugin plugin;
    @Getter
    private final Economy economy;

    private EconomyHandler(Plugin plugin){
        this.plugin = plugin;
        this.economy = initEconomy();
    }

    private Economy initEconomy() {
        RegisteredServiceProvider<Economy> econProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        System.out.println("provider is: " + econProvider);
        return econProvider == null ? null : econProvider.getProvider();
    }

    public boolean supportsEconomy() {
        return economy != null;
    }
    

    @Getter
    private static EconomyHandler economyHandler;

    public static void init(Plugin plugin) {
        economyHandler = new EconomyHandler(plugin);
    }

}