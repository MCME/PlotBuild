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
        if(plot.isOwner(invitedPlayer)) {
            sendAlreadyOwnerMessage(cs, invitedPlayer.getName());
            return;
        }
        if(plot.countOwners()>=8) {
            sendMaxTeamSize(cs);
            return;
        }
        
        if(plot.getPlotbuild().hasUnfinishedPlot(invitedPlayer)) {
            sendAlreadyMemberMessage(cs, invitedPlayer.getName());
            return;
        }
        if(plot.getPlotbuild().isBanned(invitedPlayer)) {
            sendPlayerBannedMessage(cs, invitedPlayer.getName());
            return;
        }
        if(!plot.invite(invitedPlayer)) {
            sendNoSignPlaceMessage(cs);
        }
        sendInvitedMessage(cs, invitedPlayer.getName());
        sendInvitedPlayerMessage(cs, invitedPlayer, plot.getPlotbuild().getName(), plot.getID());
        for(OfflinePlayer builder: plot.getOfflineOwners()) {
            if(builder.getPlayer()!=cs && builder!=invitedPlayer) {
                sendOtherBuilderMessage(cs, builder, invitedPlayer, plot.getPlotbuild().getName(), plot.getID());
            }
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" invited "+invitedPlayer.getName()+" to plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendAlreadyMemberMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, name + " is already owner of another plot in this plotbuild.");
    }
    
        protected void sendPlotbuildPrivateMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plotbuild is private. Ask staff if you want additional players to be assigned to your plot.");
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
                                                     + " by "+ cs.getName()+". For build instructions type: /plot info");
    }
    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer invited, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, cs.getName() + " invited " + invited.getName() 
                                                     + " to plot #"+id
                                                     + " of plotbuild " + name+".");
    }
}
