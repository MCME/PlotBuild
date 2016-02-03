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
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotAccept extends InsidePlotCommand {
    
    public PlotAccept(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Accepts a finished plot.");
        setUsageDescription(": When inside a finished plot, accepts the build inside the plot, removes the build perms for builders and the borders and messages the builders.");
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
        /*if(plot.getState()!=PlotState.FINISHED) {
            sendNotFinishedMessage(cs);
            return;
        }*/
        try {
            plot.accept();
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotAccept.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendAcceptMessage(cs);
        for(UUID builder: plot.getOfflineOwners()) {
            if(!builder.equals(((Player)cs).getUniqueId())) {
                sendBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
            }
        }
        PlotBuild plotbuild = plot.getPlotbuild();
        plotbuild.log(((Player) cs).getName()+" accepted plot "+plot.getID()+".");
        PluginData.saveData();
        if(plotbuild.countUnfinishedPlots()==0) {
            PluginData.getConfFactory().startQuery((Player)cs, getLastPlotAcceptedQuery(plotbuild), plotbuild, true);
        }
    }

    private void sendAcceptMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You accepted this plot.");
    }

    private void sendNotFinishedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was not marked as finished. You can remove it with /plot delete -k.");
    }

    private void sendBuilderMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was accepted by "+ cs.getName()+".");
    }

    private String getLastPlotAcceptedQuery(PlotBuild plotbuild) {
        return "You accepted the last plot of the plotbuild "+plotbuild.getName()
                +". Do you want to end this plotbuild now? Type 'yes' or 'no' in chat.";
    }
    
}
