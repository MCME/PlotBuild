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
import com.mcmiddleearth.pluginutil.plotStoring.InvalidRestoreDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotUnclaim extends InsidePlotCommand {
    
    public PlotUnclaim(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Unclaims a plot.");
        setUsageDescription(": When inside a plot and user is builder of plot, unclaims and resets plot. Turns border color to white. Can not be used when multiple players own the plot. /plot leave shall be used until only one builder remains.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage = "";
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.countOwners()>1) {
            sendMoreOwnersMessage(cs);
            return;
        }
        if(plot.getState()!=PlotState.CLAIMED) {
            sendNotClaimedMessage(cs);
            return;
        }
        try {
            if(!plot.unclaim()){
                sendNoSignPlaceMessage(cs);
            }
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
            logMessage = " There was an error during clearing of the plot.";
        }
        sendPlotUnclaimedMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" unclaimed plot "+plot.getID()+"."+logMessage);
        PluginData.saveData();
    }

   private void sendNotClaimedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can not unclaim a plot which was marked as finished before. If you don't want to continue, please tell a staff.");
    }

    private void sendMoreOwnersMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There are more owners of this plot. Try to use /plot leave instead.");
    }

    private void sendPlotUnclaimedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You unlcaimed this plot.");
    }
    
}
