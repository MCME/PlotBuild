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
public class PlotUnclaim extends InsidePlotCommand {
    
    public PlotUnclaim(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getOwners().size()>1) {
            sendMoreOwnersMessage(cs);
            return;
        }
        if(plot.getState()!=PlotState.CLAIMED) {
            sendNotClaimedMessage(cs);
            return;
        }
        plot.unclaim();
        sendPlotUnclaimedMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" unclaimed plot "+plot.getID()+".");
        PluginData.saveData();
    }

   private void sendNotClaimedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can not unclaim a plot which was marked as finished before. If you don't want to continue, please tell a staff.");
    }

    private void sendMoreOwnersMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There are more owners of this plot. Try to use /plot leave instead.");
    }

    private void sendPlotUnclaimedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You unlcaimed this plot.");
    }
    
}
