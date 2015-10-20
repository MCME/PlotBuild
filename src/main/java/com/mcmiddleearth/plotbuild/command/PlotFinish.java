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
public class PlotFinish extends InsidePlotCommand {
    
    public PlotFinish(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": markes a plot as finished for check.");
        setUsageDescription(": Can be used by builders when inside a plot to mark a plot as being finished and ready for check. Turns plot borders to blue wool.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getState()==PlotState.FINISHED) {
            sendPlotAlreadyFinishedMessage(cs);
            return;
        }
        plot.finish();
        sendFinishedMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" finished plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendFinishedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You marked this plot as finished. Please wait for a staff to review it.");
    }

    private void sendPlotAlreadyFinishedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot is already marked as finished.");
    }
    
}
