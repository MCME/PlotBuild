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
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import com.mcmiddleearth.pluginutil.plotStoring.InvalidRestoreDataException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Ivan1pl
 */
public class PlotEnd extends PlotBuildCommand {

    public PlotEnd(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Ends plotbuild.");
        setUsageDescription(" <name> [-k]: Ends plotbuild <name>. With [-k] keeps current plot state. Otherwise all plots that have not yet been accepted are reset to their initial states.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        boolean keep = (args.length == 2 && args[1].equals("-k"));
        PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
        if(plotbuild == null) {
            sendNoPlotbuildFoundMessage(cs);
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        PluginData.getConfFactory().startQuery((Player)cs,getSecurityQuery(plotbuild,keep), plotbuild, keep);
        //this query calls endPlotBuild
    }
    
    public static void endPlotBuild(final Player cs, final PlotBuild plotbuild, boolean keep) {
        for(Plot p : plotbuild.getPlots()) {
            if(p.getState() != PlotState.REMOVED) {
                try {
                    for(UUID builder: p.getOfflineOwners()) {
                        if(!builder.equals(((Player)cs).getUniqueId())) {
                            sendBuilderDeletedMessage(cs, Bukkit.getOfflinePlayer(builder), p.getPlotbuild().getName(), p.getID());
                        }
                    }
                    p.delete(keep);
                } catch (InvalidRestoreDataException ex) {
                    Logger.getLogger(PlotEnd.class.getName()).log(Level.SEVERE, null, ex);
                    sendRestoreErrorMessage(cs);
                }
            }
        }
        // Wait 30 ticks until restoring of entities in deleted plots is finished
        new BukkitRunnable() {
            @Override
            public void run() {
                if(PluginData.deletePlotBuild(plotbuild)) {
                    sendPlotbuildDeletedMessage(cs);
                } else {
                    sendPlotbuildDeleteFailedMessage(cs);
                }
            }
        }.runTaskLater(PlotBuildPlugin.getPluginInstance(), 30); 
    }
    
    private static void sendPlotbuildDeletedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Plotbuild successfully ended.");
    }
    
    private static void sendPlotbuildDeleteFailedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Failed to delete plotbuild files.");
    }

    private static void sendBuilderDeletedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild  " + name 
                                                     + " was removed by "+ cs.getName()+" as the plotbuild ended.");
    }
    
    public static void sendAbordMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You cancelled the removal of the plotbuild.");
    }
    
    private String getSecurityQuery(PlotBuild plotbuild, boolean keep) {
        String query = "In plotbuild "+plotbuild.getName();
        int unfinished = plotbuild.countUnfinishedPlots();
        if(unfinished==0) {
            query = query + " are no open plots. ";
        }
        else {
            String spl1=" are ", spl2=" plots", spl3="They", spl4 = "were";
            if(unfinished == 1) {
                spl1 = " is "; spl2 = " plot"; spl3 = "It"; spl4 = "was";
            }
            query = query + spl1 + unfinished + spl2 + " which "+spl4+" not accepted yet. ";
            if(keep) {
                query = query + spl3 + " will be kept as they are now. ";
            }
            else {
                query = query + spl3 + " will be restored to initial state. ";
            }
        }
        return query + "Are you sure to end this plotbuild? Type 'yes' or 'no' in chat.";
    }

}
