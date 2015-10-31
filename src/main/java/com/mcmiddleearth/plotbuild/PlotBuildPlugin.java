/* 
 *  Copyright (C) 2015 Minecraft Middle Earth
 * 
 *  This file is part of PlotBuild.
 * 
 *  PlotBuild is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PlotBuild is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PlotBuild.  If not, see <http://www.gnu.org/licenses/>.
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
