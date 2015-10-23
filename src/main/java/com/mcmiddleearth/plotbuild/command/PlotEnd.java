/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Ivan1pl
 */
public class PlotEnd extends PlotBuildCommand {
    
    public PlotEnd(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Ends plotbuild.");
        setUsageDescription(" <name> [-k]: Ends plotbuild <name>. With [-k] keeps current plot state. Otherwise all plots that that have not yet been accepted are reset to their initial states.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        boolean keep = (args.length == 2 && args[1].equals("-k"));
        PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
        if(plotbuild == null) {
            sendNoPlotbuildFoundMessage(cs);
            return;
        }
        for(Plot p : plotbuild.getPlots()) {
            if(p.getState() != PlotState.REMOVED) {
                try {
                    for(OfflinePlayer builder: p.getOwners()) {
                        if(builder.getPlayer()!=cs) {
                            sendBuilderDeletedMessage(cs, builder, p.getPlotbuild().getName(), p.getID());
                        }
                    }
                    p.delete(keep);
                } catch (InvalidRestoreDataException ex) {
                    Logger.getLogger(PlotEnd.class.getName()).log(Level.SEVERE, null, ex);
                    sendRestoreErrorMessage(cs);
                }
            }
        }
        if(PluginData.deletePlotBuild(plotbuild)) {
            sendPlotbuildDeletedMessage(cs);
        } else {
            sendPlotbuildDeleteFailedMessage(cs);
        }
    }
    
    private void sendPlotbuildDeletedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Plotbuild successfully deleted.");
    }
    
    private void sendPlotbuildDeleteFailedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Failed to delete plotbuild files.");
    }

    private void sendBuilderDeletedMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id
                                                     + " of plotbuild " + name 
                                                     + " was removed by "+ cs.getName()+" as the plotbuild ended.");
    }

}
