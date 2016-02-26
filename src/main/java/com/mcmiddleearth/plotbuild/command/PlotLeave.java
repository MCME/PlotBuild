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
public class PlotLeave extends InsidePlotCommand {
    
    public PlotLeave(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": player leaves the building team.");
        setUsageDescription(": When standing inside a plot and plot is owned by multiple players, leaves the building team.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.countOwners()<2) {
            sendNoMoreOwnersMessage(cs);
            return;
        }
        if(!plot.leave((Player)cs)){
            sendNoSignPlaceMessage(cs);
        }
        sendPlotLeaveMessage(cs);
        for(UUID builder: plot.getOfflineOwners()) {
            sendOtherBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" left plot "+plot.getID()+".");
        PluginData.saveData();
    }
    
    private void sendNoMoreOwnersMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There are no other owners of this plot. Try /plot unclaim instead.");
    }

    private void sendPlotLeaveMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You left this plot.");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " left the build team" 
                                                     + " of plot #"+id
                                                     + " of plotbuild " + name+".");
    }
  
}
