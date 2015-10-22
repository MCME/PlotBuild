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
 * @author Ivan1pl
 */
public class PlotRefuse extends InsidePlotCommand {
    
    public PlotRefuse(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Markes a plot for improvement.");
        setUsageDescription(": When inside a finished plot can be used if there are still changes needed. Turns border to yellow wool. ");
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
        plot.refuse();
        sendRefuseMessgage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" refused plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendRefuseMessgage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You refused this plot.");
    }
    
}
