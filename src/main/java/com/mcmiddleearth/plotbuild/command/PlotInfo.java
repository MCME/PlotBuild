/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotInfo extends InsidePlotCommand{
    
    public PlotInfo(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": shows building guide link for a plot");
        setUsageDescription(": When inside a plot shows the link to the related building guide. Shows also a list of staff members.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getPlotbuild().getInfo()==null) {
            sendNoInfoMessage(cs);
        }
        else {
            sendInfoMessage(cs, plot.getPlotbuild().getInfo());
        }
        sendStaffHeaderMessage(cs, plot.getPlotbuild().getName());
        for(OfflinePlayer staff : plot.getPlotbuild().getStaffList()) {
            sendStaffMessage(cs, staff.getName());
        }
    }

    private void sendNoInfoMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Sorry there is no bulding guide for this plot. Please ask staff for instructions.");
    }

    private void sendInfoMessage(CommandSender cs, String info) {
        MessageUtil.sendInfoMessage(cs, "There is a building guide for this plot at "+info+".");
    }

    private void sendStaffHeaderMessage(CommandSender cs, String name) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "Staff for plotbuild "+name+":");
    }

    private void sendStaffMessage(CommandSender cs, String name) {
        MessageUtil.sendNoPrefixInfoMessage(cs, name);
    }

}
