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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class InsidePlotCommand extends AbstractCommand {
    
    public InsidePlotCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        super (minArgs, playerOnly, permissionNodes);
    }
    

    protected Plot checkInPlot(Player player) {
        Plot plot = PluginData.getPlotAt((player).getLocation());
        if(plot == null || plot.getState()==PlotState.REMOVED) {
            sendNotInPlotMessage(player);
            return null;
        }
        return plot;
    }
    
    protected Plot checkInOwnedPlot(Player player) {
        Plot plot = checkInPlot(player);
        if(plot!=null) {
            if(plot.isOwner(player)) {
                return plot;
            }
            else {
                sendNotOwnerMessage(player);
            }
        }
        return null;
    }

    protected void sendNotInPlotMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You are not in a plot.");
    }
    
    private void sendNotOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not owner of this plot.");
    }
    



}
