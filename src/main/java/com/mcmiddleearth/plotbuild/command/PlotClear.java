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

import com.mcmiddleearth.plotbuild.PlotBuildPlugin;
import static com.mcmiddleearth.plotbuild.command.AbstractCommand.sendNoSignPlaceMessage;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import com.mcmiddleearth.pluginutil.plotStoring.InvalidRestoreDataException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotClear extends InsidePlotCommand {
    
    public PlotClear(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Resets a plot to its original state.");
        setUsageDescription(" [-u]: When inside a plot resets the plot to the initial state. -u is an optional flag, if used it does also unclaim the plot, default just clears the plot but does not unclaim it.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.isSaveInProgress()) {
            sendPlotNotReadyMessage(cs);
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plot.getPlotbuild())) {
            return;
        }
        if(plot.getState().equals(PlotState.UNCLAIMED)) {
            sendUnclaimedPlotErrorMessage(cs);
            return;
        }
        boolean unclaim=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-u")) {
            unclaim = true;
        }
        if(plot.isUsingRestoreData()) {
            plot.reset(new ClearFinishTask(plot,cs,unclaim));
        } else {
            new ClearFinishTask(plot,cs,unclaim).runTask(PlotBuildPlugin.getPluginInstance());
        }
    }
    
    private class ClearFinishTask extends CommandExecutionFinishTask {
        
        private Plot plot;
        private boolean unclaim;
        
        public ClearFinishTask(Plot plot, CommandSender cs, boolean unclaim) {
            super(cs);
            this.unclaim = unclaim;
            this.plot = plot;
        }
                
        @Override
        public void run() { 
        if(!plot.clear(unclaim)) {
            sendNoSignPlaceMessage(cs);
        }
            if(unclaim) {
                sendClearAndUnclaimMessgage(cs);
                for(UUID builder: plot.getOfflineOwners()) {
                    if(!builder.equals(((Player)cs).getUniqueId())) {
                        sendBuilderClearedAndUnclaimedMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
                    }
                }
            }
            else {
                sendClearMessage(cs);
                for(UUID builder: plot.getOfflineOwners()) {
                    sendBuilderClearedMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
                }
            }
            String logMessage = " cleared plot ";
            if(unclaim) {
                logMessage = " cleared and unclaimed plot ";
            }
            plot.getPlotbuild().log(((Player) cs).getName()+logMessage+plot.getID()+".");
            PluginData.saveData();
        }
    }

    private void sendClearAndUnclaimMessgage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared and unclaimed this plot.");
    }

    private void sendClearMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared this plot.");
    }

    private void sendBuilderClearedAndUnclaimedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was resetted to initial state and unclaimed by "+ cs.getName()+".");
    }
  
    private void sendBuilderClearedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was resetted to initial state by "+ cs.getName()+".");
    }

    private void sendUnclaimedPlotErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can't clear an unclaimed plot. Restore data are saved when a plot is claimed.");
    }
    
}
