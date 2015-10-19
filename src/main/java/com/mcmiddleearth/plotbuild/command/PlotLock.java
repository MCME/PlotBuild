/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotLock extends PlotBuildCommand {
    
    public PlotLock(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                plotbuild.setLocked(true);
                plotbuild.log(((Player) cs).getName()+" locked the plotbuild.");
            }
            sendLockedAllMessage(cs);
        }
        else {
            PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild == null) {
                sendNoPlotbuildFoundMessage(cs);
                return;
            }
            plotbuild.setLocked(true);
            sendLockedPlotbuild(cs,plotbuild.getName());
            plotbuild.log(((Player) cs).getName()+" locked the plotbuild.");
        }
        PluginData.saveData();
    }

    private void sendLockedAllMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You locked all plotbuilds.");
    }

    private void sendLockedPlotbuild(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You locked the plotbuild "+name+".");
    }
    
}
