/* 
 *  Copyright (C) 2015 Minecraft Middle Earth
 * 
 *  This file is part of PlotBuild.
 * 
 *  PlotBuild is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PlotBuild is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PlotBuild.  If not, see <http://www.gnu.org/licenses/>.
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
        for(OfflinePlayer staff : plot.getPlotbuild().getOfflineStaffList()) {
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
