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
package com.mcmiddleearth.plotbuild.listeners;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.List;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener{
    
    @EventHandler
    public void playerJoins(PlayerJoinEvent event) {
        List<String> messages = PluginData.getOfflineMessagesFor(event.getPlayer());
        if(messages!=null) {
            for(String message: messages) {
                MessageUtil.sendInfoMessage(event.getPlayer(), message);
            }
            PluginData.deleteOfflineMessagesFor(event.getPlayer());
        }
    }
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Selection selection = PluginData.getCurrentSelection(player);
        if(player.getItemInHand().getType().equals(Material.FEATHER)) {
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) && PluginData.canSelectArea(player)){
        	selection.setFirstPoint(event.getClickedBlock().getLocation());
        	sendFirstPointSetMessage(player, selection);
            }
            else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && PluginData.canSelectArea(player)){
        	selection.setSecondPoint(event.getClickedBlock().getLocation());
        	sendSecondPointSetMessage(player, selection);
            }
            event.setCancelled(true);
        } else {
            boolean fullProtection = PluginData.getProtectedWorlds().contains(event.getClickedBlock().getWorld().getName());
            if(PluginData.hasPermissionsToBuild(event.getPlayer(), event.getClickedBlock().getLocation())) {
                Plot plot = PluginData.getPlotAt(event.getClickedBlock().getLocation());
                if(plot != null && plot.isOwner(event.getPlayer()) && plot.getPlotbuild().isLocked() &&
                        !plot.getPlotbuild().isStaff(event.getPlayer()) && !event.getPlayer().hasPermission("plotbuild.staff")) {
                    sendPlotbuildLockedMessage(event.getPlayer());
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setCancelled(true);
                }
            } else if(fullProtection && !event.getPlayer().hasPermission("plotbuild.trusted")) {
                sendNotAllowedToBuildMessage(event.getPlayer());
                event.setUseItemInHand(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if(event.getFrom().getBlock()!=event.getTo().getBlock()) {
            Player player = event.getPlayer();
            Plot plot = PluginData.getPlotAt(event.getTo());
            List playersInOwnPlot = PluginData.getPlayersInOwnPlot();
            if(plot != null && plot.getState()!=PlotState.REMOVED && plot.isOwner(player)) {
                if(player.getGameMode()==GameMode.SURVIVAL) {
                    if(!playersInOwnPlot.contains(player)) {
                        playersInOwnPlot.add(player);
                    }
                    player.setGameMode(GameMode.CREATIVE);
                }
            }
            else {
                if(playersInOwnPlot.contains(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                    playersInOwnPlot.remove(player);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }
        boolean fullProtection = PluginData.getProtectedWorlds().contains(event.getBlock().getWorld().getName());
        if(PluginData.hasPermissionsToBuild(event.getPlayer(), event.getBlock().getLocation())) {
            Plot plot = PluginData.getPlotAt(event.getBlock().getLocation());
            if(plot != null && plot.isOwner(event.getPlayer()) && plot.getPlotbuild().isLocked() &&
                    !plot.getPlotbuild().isStaff(event.getPlayer()) && !event.getPlayer().hasPermission("plotbuild.staff")) {
                sendPlotbuildLockedMessage(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        } else if(fullProtection && !event.getPlayer().hasPermission("plotbuild.trusted")) {
            sendNotAllowedToBuildMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }
    
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }
        boolean fullProtection = PluginData.getProtectedWorlds().contains(event.getBlock().getWorld().getName());
        if(PluginData.hasPermissionsToBuild(event.getPlayer(), event.getBlock().getLocation())) {
            Plot plot = PluginData.getPlotAt(event.getBlock().getLocation());
            if(plot != null && plot.isOwner(event.getPlayer()) && plot.getPlotbuild().isLocked() &&
                    !plot.getPlotbuild().isStaff(event.getPlayer()) && !event.getPlayer().hasPermission("plotbuild.staff")) {
                sendPlotbuildLockedMessage(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        } else if(fullProtection && !event.getPlayer().hasPermission("plotbuild.trusted")) {
            sendNotAllowedToBuildMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }
    
    @EventHandler
    public void onPlaceHanging(HangingPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }
        boolean fullProtection = PluginData.getProtectedWorlds().contains(event.getEntity().getWorld().getName());
        if(PluginData.hasPermissionsToBuild(event.getPlayer(), event.getEntity().getLocation())) {
            Plot plot = PluginData.getPlotAt(event.getEntity().getLocation());
            if(plot != null && plot.isOwner(event.getPlayer()) && plot.getPlotbuild().isLocked() &&
                    !plot.getPlotbuild().isStaff(event.getPlayer()) && !event.getPlayer().hasPermission("plotbuild.staff")) {
                sendPlotbuildLockedMessage(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        } else if(fullProtection && !event.getPlayer().hasPermission("plotbuild.trusted")) {
            sendNotAllowedToBuildMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }
    
    @EventHandler
    public void onBreakHanging(HangingBreakByEntityEvent event) {
        if(event.isCancelled()) {
            return;
        }
        boolean fullProtection = PluginData.getProtectedWorlds().contains(event.getEntity().getWorld().getName());
        Player player = (Player) event.getRemover();
        if(PluginData.hasPermissionsToBuild(player, event.getEntity().getLocation())) {
            Plot plot = PluginData.getPlotAt(event.getEntity().getLocation());
            if(plot != null && plot.isOwner(player) && plot.getPlotbuild().isLocked() &&
                    !plot.getPlotbuild().isStaff(player) && !player.hasPermission("plotbuild.staff")) {
                sendPlotbuildLockedMessage(player);
                event.setCancelled(true);
                return;
            }
        } else if(fullProtection && !player.hasPermission("plotbuild.trusted")) {
            sendNotAllowedToBuildMessage(player);
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }
    
    private void sendFirstPointSetMessage(Player player, Selection sel) {
        String message = "First point set";
        if(sel.isValid()) {
            message += " (area: " + Integer.toString(sel.getArea()) + " blocks, volume: "
                    + Integer.toString(sel.getVolume()) + " blocks)";
        }
        message += ".";
        MessageUtil.sendInfoMessage(player, message);
    }
    
    private void sendSecondPointSetMessage(Player player, Selection sel) {
        String message = "Second point set";
        if(sel.isValid()) {
            message += " (area: " + Integer.toString(sel.getArea()) + " blocks, volume: "
                    + Integer.toString(sel.getVolume()) + " blocks)";
        }
        message += ".";
        MessageUtil.sendInfoMessage(player, message);
    }
    
    private void sendPlotbuildLockedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plotbuild is locked. Try again later.");
    }
    
    private void sendNotAllowedToBuildMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not allowed to build here.");
    }

}
