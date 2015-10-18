/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotLeave extends InsidePlotCommand {
    
    public PlotLeave(String... permissionNodes) {
        super(0, true, permissionNodes);
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
        PluginData.saveData();
    }
    
    private void sendNoMoreOwnersMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are no other owners of this plot. Try /plot unclaim instead.");
    }

    private void sendPlotLeaveMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You left this plot.");
    }

  
}
