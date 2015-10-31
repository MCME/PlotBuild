/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotClear extends InsidePlotCommand {
    
    public PlotClear(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Resets a plot to its original state.");
        setUsageDescription(" [-u]: When inside a plot resets the plot to the initial state. -u is an optional flag, if used it does also unclaim the plot, default just clears the plot but does not unclaim it.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plot.getPlotbuild())) {
            return;
        }
        boolean unclaim=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-u")) {
            unclaim = true;
            sendClearAndUnclaimMessgage(cs);
            for(OfflinePlayer builder: plot.getOwners()) {
                if(builder.getPlayer()!=cs) {
                    sendBuilderClearedAndUnclaimedMessage(cs, builder, plot.getPlotbuild().getName(), plot.getID());
                }
            }
        }
        else {
            sendClearMessage(cs);
            for(OfflinePlayer builder: plot.getOwners()) {
                sendBuilderClearedMessage(cs, builder, plot.getPlotbuild().getName(), plot.getID());
            }
        }
        try {
            plot.clear(unclaim);
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotClear.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
        }
        String logMessage = " cleared plot ";
        if(unclaim) {
            logMessage = " cleared and unclaimed plot ";
        }
        plot.getPlotbuild().log(((Player) cs).getName()+logMessage+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendClearAndUnclaimMessgage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared  and unclaimed this plot.");
    }

    private void sendClearMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared this plot.");
    }

    private void sendBuilderClearedAndUnclaimedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was resetted to initial state and unclaimed by "+ cs.getName()+".");
    }
  
    private void sendBuilderClearedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was resetted to initial state by "+ cs.getName()+".");
    }
    
}
