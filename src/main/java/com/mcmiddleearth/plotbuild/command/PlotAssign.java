/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
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
        OfflinePlayer assignedPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(assignedPlayer.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plot.getOwners().contains(assignedPlayer)) {
            sendAlreadyOwnerMessage(cs, assignedPlayer.getName());
            return;
        }
        if(plot.getPlotbuild().getBannedPlayers().contains(assignedPlayer)) {
            sendPlayerBannedMessage(cs, assignedPlayer.getName());
            return;
        }
        if(plot.getOwners().size()>=8) {
            sendMaxTeamSize(cs);
        }
        if(plot.getState()==PlotState.UNCLAIMED) {
            plot.claim(assignedPlayer);
        }
        else {
            plot.invite(assignedPlayer);
        }
        sendAssignedMessage(cs, assignedPlayer.getName());
        plot.getPlotbuild().log(((Player) cs).getName()+" asigned "+assignedPlayer.getName()+" to plot "+plot.getID()+".");
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


}
