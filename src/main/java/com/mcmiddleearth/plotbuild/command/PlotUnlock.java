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
public class PlotUnlock extends PlotBuildCommand {
    
    public PlotUnlock(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Unlocks plotbuilds.");
        setUsageDescription(" [name]: Unlocks the plotbuild [name]. If [name] is not specified all plotbuilds are unlocked.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                plotbuild.setLocked(false);
                plotbuild.log(((Player) cs).getName()+" unlocked the plotbuild.");
            }
            sendLockedAllMessage(cs);
        }
        else {
            PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild == null) {
                sendNoPlotbuildFoundMessage(cs);
                return;
            }
            plotbuild.setLocked(false);
            plotbuild.log(((Player) cs).getName()+" unlocked the plotbuild.");
            sendLockedPlotbuild(cs,plotbuild.getName());
        }
        PluginData.saveData();
    }
    
    private void sendLockedAllMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You unlocked all plotbuilds.");
    }

    private void sendLockedPlotbuild(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You unlocked the plotbuild "+name+".");
    }
}
