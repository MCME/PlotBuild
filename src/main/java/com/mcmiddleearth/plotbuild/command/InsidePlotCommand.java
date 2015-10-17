/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

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
        if(plot == null) {
            sendNotInPlotMessage(player);
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

    private void sendNotOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not owner of this plot.");
    }
    

}
