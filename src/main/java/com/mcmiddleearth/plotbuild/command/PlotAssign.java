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

import com.mcmiddleearth.plotbuild.constants.Permission;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotAssign extends InsidePlotCommand {
    
    public PlotAssign(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Adds a player to the builders of a plot.");
        setUsageDescription(" <player>: Assigns a player to the plot the person issuing the command stands in, multiple players can be assigned to one plot. ");
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
        OfflinePlayer assignedPlayer = BukkitUtil.matchPlayer(args[0]);
        if(assignedPlayer==null) {
            assignedPlayer = Bukkit.getOfflinePlayer(args[0]);
        }
        if(assignedPlayer.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plot.isOwner(assignedPlayer)) {
            sendAlreadyOwnerMessage(cs, assignedPlayer.getName());
            return;
        }
        if(plot.getPlotbuild().isBanned(assignedPlayer)) {
            sendPlayerBannedMessage(cs, assignedPlayer.getName());
            return;
        }
        if(plot.countOwners()>=8) {
            sendMaxTeamSize(cs);
            return;
        }
        if(plot.isUsingRestoreData()) {
            try {
                //1.13 remove: PluginData.savePlotRestoreData(plot);
                plot.save();
            } catch (IOException ex) {
                sendRestoreDataErrorMessage(cs, plot);
                return;
            }
        }
        boolean signsPlaced;
        if(plot.getState()==PlotState.UNCLAIMED) {
            signsPlaced = plot.claim(assignedPlayer);
        }
        else {
            signsPlaced = plot.invite(assignedPlayer);
        }
        if(!signsPlaced) {
            sendNoSignPlaceMessage(cs);
        }
        sendAssignedMessage(cs, assignedPlayer.getName());
        if(cs!=assignedPlayer.getPlayer()) {
            sendAssignedPlayerMessage(cs, assignedPlayer, plot.getPlotbuild().getName(), plot.getID());
        }
        for(UUID builder: plot.getOfflineOwners()) {
            if(!builder.equals(((Player)cs).getUniqueId()) && !builder.equals(assignedPlayer.getUniqueId())) {
                sendOtherBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), 
                                        assignedPlayer, plot.getPlotbuild().getName(), plot.getID());
            }
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" assigned "+assignedPlayer.getName()+" to plot #"+plot.getID()+".");
        PluginData.saveData();
        }
  
    private void sendAlreadyMemberMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name + " is already owner of an other plot in this plotbuild.");
    }

    private void sendAlreadyOwnerMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name +" is already owner of this plot.");
    }

    private void sendAssignedMessage(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You assigned "+ name+" to this plot.");
    }
    
    private void sendPlayerBannedMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name +" is banned from this plotbuild.");
    }

    private void sendMaxTeamSize(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There can't be more builder in this plot.");
    }

    private void sendAssignedPlayerMessage(CommandSender cs, OfflinePlayer assignedPlayer, String plotbuild, int ID) {
        MessageUtil.sendOfflineMessage(assignedPlayer, "You were assigned to plot #" + ID
                                                     + " of plotbuild " + plotbuild 
                                                     + " by "+ cs.getName()+". For build instructions type: /plot info");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer assigned, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " assigned " + assigned.getName() 
                                                     + " to plot #"+id
                                                     + " of plotbuild " + name+".");
    }

    private void sendNoUserPerm(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "The player you want to assign, has no permission to use plotbuild.");
    }
    
    private void sendRestoreDataErrorMessage(CommandSender cs, Plot plot) {
        MessageUtil.sendErrorMessage(cs, "Failed to save restore data for this plot. Ask staff for help.");
        for(UUID staffId: plot.getPlotbuild().getOfflineStaffList()) {
            MessageUtil.sendOfflineMessage(Bukkit.getOfflinePlayer(staffId),"Failed to save restore data for plot #"
                                           +plot.getID()+" of plotbuild "+ plot.getPlotbuild().getName()+"."); 
        }
    }
}
