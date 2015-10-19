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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotDelete extends InsidePlotCommand {
    
    public PlotDelete(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage1 = " deleted ", logMessage2 = "";
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plot.getPlotbuild())) {
            return;
        }
        boolean keep=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-k")) {
            keep = true;
            sendDeleteAndKeepMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" claimed plot "+plot.getID()+".");
        }
        else {
            sendDeleteMessage(cs);
            logMessage1 =  " deleted and cleared ";
        }
        try {
            plot.delete(keep);
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
            logMessage1 = " deleted ";
            logMessage2 = " There was an error during clearing of the plot.";
        }
        plot.getPlotbuild().log(((Player) cs).getName()+logMessage1+"plot "+plot.getID()+"."+logMessage2);
        PluginData.saveData();
    }

    private void sendDeleteMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and cleared the changes within.");
    }

    private void sendDeleteAndKeepMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and kept the changes within.");
    }
    
}
