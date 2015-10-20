/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotUnclaim extends InsidePlotCommand {
    
    public PlotUnclaim(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Unclaims a plot.");
        setUsageDescription(": When inside a plot and user is builder of plot, unclaims and resets plot. Turns border color to white. Can not be used when multiple players own the plot. /plot leave shall be used until only one builder remains.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage = "";
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
        try {
            plot.unclaim();
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
            logMessage = " There was an error during clearing of the plot.";
        }
        sendPlotUnclaimedMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" unclaimed plot "+plot.getID()+"."+logMessage);
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
