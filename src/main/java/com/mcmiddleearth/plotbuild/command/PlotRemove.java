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

import static com.mcmiddleearth.plotbuild.command.AbstractCommand.sendRestoreErrorMessage;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
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
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotRemove extends InsidePlotCommand {
    
    public PlotRemove(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Removes a player from the builders of a plot.");
        setUsageDescription(" <player>: Removes a player from the building team of the plot the person issuing the command stands in. ");
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
        OfflinePlayer removedPlayer = BukkitUtil.matchPlayer(args[0]);
        if(removedPlayer==null) {
            removedPlayer = Bukkit.getOfflinePlayer(args[0]);
        }
        if(!plot.isOwner(removedPlayer)) {
            sendNotOwnerMessage(cs,removedPlayer.getName());
            return;
        }
        String logMessage="";
        if(plot.countOwners()==1) {
            try {
                if(!plot.unclaim()){
                    sendNoSignPlaceMessage(cs);
                }
            } catch (InvalidRestoreDataException ex) {
                Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
                sendRestoreErrorMessage(cs);
                logMessage = " There was an error during clearing of the plot.";
            }
        } else {
            if(!plot.remove(removedPlayer)) {
                sendNoSignPlaceMessage(cs);
            }
        }
        boolean signsPlaced;
        sendRemovedMessage(cs, removedPlayer.getName());
        if(cs!=removedPlayer.getPlayer()) {
            sendRemovedPlayerMessage(cs, removedPlayer, plot.getPlotbuild().getName(), plot.getID());
        }
        for(UUID builder: plot.getOfflineOwners()) {
            if(!builder.equals(((Player)cs).getUniqueId()) && !builder.equals(removedPlayer.getUniqueId())) {
                sendOtherBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), 
                                        removedPlayer, plot.getPlotbuild().getName(), plot.getID());
            }
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" removed "+removedPlayer.getName()+" from plot #"+plot.getID()+"."+logMessage);
        PluginData.saveData();
        }
  
    private void sendNotOwnerMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name + " is not in the building team of this plot.");
    }

    private void sendRemovedMessage(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You removed "+ name+" from buiding team of this plot.");
    }
    
    private void sendRemovedPlayerMessage(CommandSender cs, OfflinePlayer assignedPlayer, String plotbuild, int ID) {
        MessageUtil.sendOfflineMessage(assignedPlayer, "You were removed from plot #" + ID
                                                     + " of plotbuild " + plotbuild 
                                                     + " by "+ cs.getName()+".");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer assigned, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " removed " + assigned.getName() 
                                                     + " from plot #"+id
                                                     + " of plotbuild " + name+".");
    }

}
