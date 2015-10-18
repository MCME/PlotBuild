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
 * @author Eriol_Eandur
 */
public abstract class InsidePlotCommand extends AbstractCommand {
    
    public InsidePlotCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        super (minArgs, playerOnly, permissionNodes);
    }
    

    protected Plot checkInPlot(Player player) {
        Plot plot = PluginData.getPlotAt((player).getLocation());
        if(plot == null || plot.getState()==PlotState.REMOVED) {
            sendNotInPlotMessage(player);
            return null;
        }
        return plot;
    }
    
    protected Plot checkInOwnedPlot(Player player) {
        Plot plot = checkInPlot(player);
        if(plot!=null) {
            if(plot.getOwners().contains(player)) {
                return plot;
            }
            else {
                sendNotOwnerMessage(player);
            }
        }
        return null;
    }

    protected void sendNotInPlotMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You are not in a plot.");
    }
    
    protected void sendRestoreErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Plot reset data doesn't fit plot size, not restoring.");
    }

    private void sendNotOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not owner of this plot.");
    }

    protected void sendPlotbuildPrivateMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plotbuild is private. Ask staff to assign you to a plot.");
    }



}
