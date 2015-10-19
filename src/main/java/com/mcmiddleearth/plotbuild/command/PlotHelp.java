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
 * @author Lars
 */
public class PlotHelp extends InsidePlotCommand{
    
    public PlotHelp(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getPlotbuild().getInfo()==null) {
            sendNoHelpMessage(cs);
        }
        else {
            sendHelpMessage(cs, plot.getPlotbuild().getInfo());
        }
    }

    private void sendNoHelpMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Sorry there is no bulding guide for this plot. Please ask staff for instructions.");
    }

    private void sendHelpMessage(CommandSender cs, String info) {
        MessageUtil.sendInfoMessage(cs, "There is a building guide for this plot at "+info+".");
    }
}
