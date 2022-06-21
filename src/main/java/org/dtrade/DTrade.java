package org.dtrade;

import org.bukkit.plugin.java.JavaPlugin;
import org.dtrade.commands.CommandTrade;
import org.dtrade.gui.management.GuiManager;

public class DTrade extends JavaPlugin {

    static DTrade instance;

    public DTrade() {
        instance = this;
    }

    @Override
    public void onEnable() {

        new CommandTrade();
        new GuiManager();

    }

    public static DTrade getInstance() {
        return instance;
    }

}
