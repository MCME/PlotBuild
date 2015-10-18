/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotAddstaff extends PlotBuildCommand {
    
    public PlotAddstaff(String... permissionNodes) {
        super(1, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        OfflinePlayer newStaff = Bukkit.getOfflinePlayer(args[0]);
        if(newStaff==null) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        plotbuild.getStaffList().add(newStaff);
        sendAddStaffMessgage(cs, newStaff.getName(), plotbuild.getName());
        plotbuild.log(((Player) cs).getName()+" added "+newStaff.getName()+" to staff.");
        PluginData.saveData();
    }

    private void sendAddStaffMessgage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You added "+ name+" to staff of plotbuild "+plotbuild + ".");
    }
    
}
