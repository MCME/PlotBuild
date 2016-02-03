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

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        if(!plot.finish()) {
            sendNoSignPlaceMessage(cs);
        }
        sendFinishedMessage(cs);
        for(UUID builder: plot.getOfflineOwners()) {
            if(!builder.equals(((Player)cs).getUniqueId())) {
                sendBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
            }
        }
        for(UUID staff: plot.getPlotbuild().getOfflineStaffList()) {
            if(!staff.equals(((Player)cs).getUniqueId())) {
                sendStaffMessage(cs, Bukkit.getOfflinePlayer(staff), plot.getPlotbuild().getName(), plot.getID());
            }
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" finished plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendFinishedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You marked this plot as finished. Please wait for a staff to review it.");
    }

    private void sendPlotAlreadyFinishedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot is already marked as finished.");
    }
    
    private void sendBuilderMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was marked as finished by "+ cs.getName()+".");
    }

    private void sendStaffMessage(CommandSender cs, OfflinePlayer staff, String name, int id) {
        MessageUtil.sendOfflineMessage(staff, "Plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was marked as finished by "+ cs.getName()+".");
    }
}
