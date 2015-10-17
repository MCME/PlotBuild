/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotInvite extends InsidePlotCommand {
    
    public PlotInvite(String... permissionNodes) {
        super(1, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getPlotbuild().isLocked()) {
            sendPlotbuildLockedMessage(cs);
            return;
        }
        Player invitedPlayer = Bukkit.getPlayer(args[0]);
        if(invitedPlayer==null) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plot.getOwners().contains(invitedPlayer)) {
            sendAlreadyOwnerMessage(cs, invitedPlayer.getDisplayName());
            return;
        }
        if(plot.getPlotbuild().isMember(invitedPlayer)) {
            sendAlreadyMemberMessage(cs, invitedPlayer.getDisplayName());
            return;
        }
        plot.invite(invitedPlayer);
        sendInvitedMessage(cs, invitedPlayer.getDisplayName());
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

    private void sendInvitedMessage(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You invited "+ name+" to this plot.");
    }
    private void sendPlotbuildLockedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can not invite players to a plot at the moment as this plotbuild is locked. Try again later.");
    }
    
}
