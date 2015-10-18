/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotUnban extends PlotBuildCommand {
    
    public PlotUnban(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        OfflinePlayer banned = null;
        for(OfflinePlayer search : plotbuild.getBannedPlayers()) {
            if(search.getName().equals(args[0])) {
                banned = search;
                break;
            }
        }
        if(banned==null) {
            sendNotBannedMessage(cs, args[0], plotbuild.getName());
            return;
        }
        plotbuild.getBannedPlayers().remove(banned);
        sendUnbannedMessage(cs,banned.getName(),plotbuild.getName());
        PluginData.saveData();
    }

    private void sendUnbannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You unbanned "+ name+" from plotbuild "+plotbuild + ".");
    }

    private void sendNotBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendErrorMessage(cs, name+" is not banned from plotbuild "+plotbuild + ".");
    }
    
}
