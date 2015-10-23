/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Lars
 */
public class PlotStaff extends InsidePlotCommand{
    
    public PlotStaff(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Shows staff of a plotbuild.");
        setUsageDescription(" [name]: When standing inside a plot shows staff of the plotbuild. If [name] is specified the staff of the plotbuild [name] is shown.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild;
        if(args.length>0) {
            plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild==null) {
                sendNoPlotbuildFoundMessage(cs);
                return;
            }
        }
        else
        {
            Plot plot = checkInPlot((Player) cs);
            if(plot==null) {
                return;
            }
            plotbuild = plot.getPlotbuild();
        }
        sendStaffHeaderMessage(cs, plotbuild.getName());
        for(OfflinePlayer staff : plotbuild.getStaffList()) {
            sendStaffMessage(cs, staff.getName());
        }
    }

    private void sendStaffHeaderMessage(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "Staff for plotbuild "+name+":");
    }

    private void sendStaffMessage(CommandSender cs, String name) {
        MessageUtil.sendNoPrefixInfoMessage(cs, name);
    }
}
