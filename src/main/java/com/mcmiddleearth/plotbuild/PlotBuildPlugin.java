/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild;

import com.mcmiddleearth.plotbuild.command.PlotCommandExecutor;
import lombok.Getter;
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
        getCommand("plot").setExecutor(new PlotCommandExecutor());
    }
    
}
