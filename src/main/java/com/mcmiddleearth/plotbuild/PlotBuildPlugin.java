/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild;

import com.mcmiddleearth.plotbuild.command.PlotCommandExecutor;
import com.mcmiddleearth.plotbuild.conversations.PlotBuildConversationFactory;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.listeners.PlayerListener;
import com.mcmiddleearth.plotbuild.listeners.WorldListener;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Ivan1pl
 */
public class PlotBuildPlugin extends JavaPlugin {
    
    @Getter
    private static PlotBuildPlugin pluginInstance;
    
    @Override
    public void onEnable() {
        pluginInstance = this;
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getCommand("plot").setExecutor(new PlotCommandExecutor());
        PluginData.loadData();
        PluginData.setConfFactory(new PlotBuildConversationFactory(this));
        getLogger().info("Enabled!");
    }
    
    public static boolean allowBuild(Player player, Location location) {
        return PluginData.hasPermissionsToBuild(player, location);
    }
    
    public static boolean denyBuild(Player player, Location location) {
        return PluginData.hasNoPermissionsToBuild(player, location);
    }
     
}
