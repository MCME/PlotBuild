/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotSign extends InsidePlotCommand{
    
    public PlotSign(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": renews the plot signs.");
        setUsageDescription(": Use when inside a plot, the wool blocks of the border and the plot signs are placed again.");
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
        plot.placeSigns();
        sendSignsPlacedMessage(cs);
    }

    private void sendSignsPlacedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Plot signs placed.");
    }
}
