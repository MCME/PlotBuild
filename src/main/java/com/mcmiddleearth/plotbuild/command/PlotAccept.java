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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotAccept extends InsidePlotCommand {
    
    public PlotAccept(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Accepts a finished plot.");
        setUsageDescription(": When inside a finished plot, accepts the build inside the plot, removes the build perms for builders and the borders and messages the builders.");
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
        if(plot.getState()!=PlotState.FINISHED) {
            sendNotFinishedMessage(cs);
            return;
        }
        try {
            plot.accept();
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotAccept.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendAcceptMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" accepted plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendAcceptMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You accepted this plot.");
    }

    private void sendNotFinishedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was not marked as finished. You can remove it with /plot delete -k.");
    }
    
}
