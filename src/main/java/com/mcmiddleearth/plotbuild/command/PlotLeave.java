/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotLeave extends InsidePlotCommand {
    
    public PlotLeave(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": player leaves the building team.");
        setUsageDescription(": When standing inside a plot and plot is owned by multiple players, leaves the building team.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getOwners().size()<2) {
            sendNoMoreOwnersMessage(cs);
            return;
        }
        plot.leave((Player)cs);
        sendPlotLeaveMessage(cs);
        for(OfflinePlayer builder: plot.getOwners()) {
            sendOtherBuilderMessage(cs, builder, plot.getPlotbuild().getName(), plot.getID());
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" left plot "+plot.getID()+".");
        PluginData.saveData();
    }
    
    private void sendNoMoreOwnersMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are no other owners of this plot. Try /plot unclaim instead.");
    }

    private void sendPlotLeaveMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You left this plot.");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " left the build team" 
                                                     + " of plot #"+id
                                                     + " of plotbuild " + name+".");
    }
  
}
