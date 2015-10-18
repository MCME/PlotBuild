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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotAssign extends InsidePlotCommand {
    
    public PlotAssign(String... permissionNodes) {
        super(1, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        Player assignedPlayer = Bukkit.getPlayer(args[0]);
        if(assignedPlayer==null) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plot.getOwners().contains(assignedPlayer)) {
            sendAlreadyOwnerMessage(cs, assignedPlayer.getDisplayName());
            return;
        }
        if(plot.getPlotbuild().isMember(assignedPlayer)) {
            sendAlreadyMemberMessage(cs, assignedPlayer.getDisplayName());
            return;
        }
        if(plot.getState()==PlotState.UNCLAIMED) {
            plot.claim(assignedPlayer);
        }
        else {
            plot.invite(assignedPlayer);
        }
        sendAssignedMessage(cs, assignedPlayer.getDisplayName());
        PluginData.saveData();
        }
  
    private void sendPlayerNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Player not found.");
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

}
