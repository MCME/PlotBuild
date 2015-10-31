/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.listeners;

import com.mcmiddleearth.plotbuild.data.PluginData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 *
 * @author Ivan1pl
 */
public class WorldListener implements Listener {
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if(PluginData.isLoaded()) return;
        PluginData.getMissingWorlds().remove(event.getWorld().getName());
        if(PluginData.getMissingWorlds().isEmpty()) {
            PluginData.loadData();
        }
    }
    
}
