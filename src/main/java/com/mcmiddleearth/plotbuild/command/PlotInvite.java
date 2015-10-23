/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotInvite extends InsidePlotCommand {
    
    public PlotInvite(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": invites a player to join a plot team.");
        setUsageDescription(" <player>: When inside a plot a builder of the plot, adds <player> to the plot. Both players then can build inside that plot.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInOwnedPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getPlotbuild().isPriv()) {
            sendPlotbuildPrivateMessage(cs);
            return;
        }
        if(plot.getPlotbuild().isLocked()) {
            sendPlotbuildLockedMessage(cs);
            return;
        }
        OfflinePlayer invitedPlayer = Bukkit.getOfflinePlayer(args[0]);
        if(invitedPlayer.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plot.getOwners().contains(invitedPlayer)) {
            sendAlreadyOwnerMessage(cs, invitedPlayer.getName());
            return;
        }
        if(plot.getOwners().size()>=8) {
            sendMaxTeamSize(cs);
            return;
        }
        
        if(plot.getPlotbuild().hasUnfinishedPlot(invitedPlayer)) {
            sendAlreadyMemberMessage(cs, invitedPlayer.getName());
            return;
        }
        if(plot.getPlotbuild().getBannedPlayers().contains(invitedPlayer)) {
            sendPlayerBannedMessage(cs, invitedPlayer.getName());
            return;
        }
        plot.invite(invitedPlayer);
        sendInvitedMessage(cs, invitedPlayer.getName());
        sendInvitedPlayerMessage(cs, invitedPlayer, plot.getPlotbuild().getName(), plot.getID());
        for(OfflinePlayer builder: plot.getOwners()) {
            if(builder.getPlayer()!=cs && builder!=invitedPlayer) {
                sendOtherBuilderMessage(cs, builder, invitedPlayer, plot.getPlotbuild().getName(), plot.getID());
            }
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" invited "+invitedPlayer.getName()+" to plot "+plot.getID()+".");
        PluginData.saveData();
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
    
    private void sendPlayerBannedMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name +" is banned from this plotbuild.");
    }

    private void sendMaxTeamSize(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There can't be more builder in this plot.");
    }

    private void sendInvitedPlayerMessage(CommandSender cs, OfflinePlayer invited, String name, int id) {
        MessageUtil.sendOfflineMessage(invited, "You were invited to plot #"+id
                                                     + " of plotbuild " + name 
                                                     + " by "+ cs.getName()+".");
    }
    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer invited, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " invited " + invited.getName() 
                                                     + " to plot #"+id
                                                     + " of plotbuild " + name+".");
    }
}
