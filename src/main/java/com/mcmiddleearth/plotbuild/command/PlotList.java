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

/**
 *
 * @author Ivan1pl
 */
public class PlotList extends AbstractCommand {
    
    public PlotList(String... permissionNodes) {
        super(0, false, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        MessageUtil.sendInfoMessage(cs, "Running plotbuilds:");
        if(PluginData.getPlotbuildsList().isEmpty()) {
            MessageUtil.sendNoPrefixInfoMessage(cs, "There are no plotbuilds running. Check again later.");
        } else {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                MessageUtil.sendNoPrefixInfoMessage(cs, plotbuild.getName() + " (unclaimed plots: " +
                        Integer.toString(plotbuild.countUnclaimedPlots()) + ")");
            }
        }
    }
    
}
