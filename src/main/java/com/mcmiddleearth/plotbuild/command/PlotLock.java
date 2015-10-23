/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotLock extends PlotBuildCommand {
    
    public PlotLock(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Locks plotbuilds.");
        setUsageDescription(" [name]: Locks the plotbuild [name]. If [name] is not specified all plotbuilds are locked. This prevents non-staff to build on plots, claim new plots and invite players to plots. This can be used during jobs to get the people working on the plotbuilds to help on the job.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                plotbuild.setLocked(true);
                sendBuilderMessages(cs, plotbuild);
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
            sendBuilderMessages(cs, plotbuild);
            plotbuild.log(((Player) cs).getName()+" locked the plotbuild.");
        }
        PluginData.saveData();
    }

    private void sendBuilderMessages(CommandSender cs, PlotBuild plotbuild) {
                for(Plot plot: plotbuild.getPlots()) {
                    for(OfflinePlayer offlineBuilder : plot.getOwners()) {
                        Player builder = offlineBuilder.getPlayer();
                        if(builder!=null && builder!=cs) {
                            sendBuilderMessage(cs, builder, plotbuild.getName());
                        }
                    }
                }
    }
    
    private void sendLockedAllMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You locked all plotbuilds.");
    }

    private void sendLockedPlotbuild(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You locked the plotbuild "+name+".");
    }

    private void sendBuilderMessage(CommandSender cs, Player builder, String name) {
        MessageUtil.sendInfoMessage(builder, cs.getName()+" locked the plotbuild "+name+".");
    }

    
}
