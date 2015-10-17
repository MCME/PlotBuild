/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.listeners;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.data.Selection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener{
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Selection selection = PluginData.getCurrentSelection(player);
        if(player.getItemInHand().getType().equals(Material.FEATHER)){
        	if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
        		selection.setFirstPoint(event.getClickedBlock().getLocation());
        		player.sendMessage("First point set");
        	}
        	else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
        		selection.setSecondPoint(event.getClickedBlock().getLocation());
        		player.sendMessage("Second point set");
        	}
        	event.setCancelled(true);
        }
    }

}
