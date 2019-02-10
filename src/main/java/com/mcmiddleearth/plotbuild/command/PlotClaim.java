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
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotClaim extends InsidePlotCommand {
    
    public PlotClaim(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Gives build perms for an unclaimed plot.");
        setUsageDescription(": When standing inside an unclaimed plot, claims the plot. Ensures build permissions inside the plot. Border color is set to Purple. A player may only claim a plot when he does not have a claimed or refused plot.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
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
        if(plot.isOwner((Player)cs)) {
            sendAlreadyOwnerMessage(cs);
            return;
        }
        if(plot.getState()!=PlotState.UNCLAIMED) {
            sendPlotAlreadyClaimedMessage(cs);
            return;
        }
        if(plot.getPlotbuild().hasUnfinishedPlot((Player) cs)) {
            sendAlreadyMemberMessage(cs);
            return;
        }
        if(plot.getPlotbuild().isBanned((Player) cs)) {
            sendPlayerBannedMessage(cs);
            return;
        }
        if(PluginData.getClaimCooldownList().contains(((Player)cs).getUniqueId())) {
            sendCooldownErrorMessage(cs);
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
        if(!plot.claim((Player) cs)) {
            sendNoSignPlaceMessage(cs);
        }
        sendPlotClaimedMessage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" claimed plot "+plot.getID()+".");
        final UUID playerId= ((Player)cs).getUniqueId();
        PluginData.getClaimCooldownList().add(playerId);
        new BukkitRunnable() {
            @Override
            public void run() {
                PluginData.getClaimCooldownList().remove(playerId);
            }
        }.runTaskLater(PlotBuildPlugin.getPluginInstance(), PluginData.getClaimCooldownTics());
        PluginData.saveData();
    }

    private void sendPlotClaimedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You claimed this plot. For build instructions type: /plot info");
    }

        protected void sendPlotbuildPrivateMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plotbuild is private. Ask staff to assign you to a plot.");
    }

    private void sendPlotAlreadyClaimedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was already claimed by an other player, you can ask him to invite you.");
    }

    private void sendAlreadyOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already owner of this plot.");
    }

    private void sendAlreadyMemberMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already owner of another plot in this plotbuild.");
    }

    private void sendPlotbuildLockedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can not claim a plot at the moment as this plotbuild is locked. Try again later.");
    }
 
    protected void sendPlayerBannedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are banned from this plotbuild.");
    }

    private void sendCooldownErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to wait "+(PluginData.getClaimCooldownTics()/20)+" seconds after claiming a plot before you may claim another one.");
    }
    
    private void sendRestoreDataErrorMessage(CommandSender cs, Plot plot) {
        MessageUtil.sendErrorMessage(cs, "Failed to save restore data for this plot. Ask staff for help.");
        for(UUID staffId: plot.getPlotbuild().getOfflineStaffList()) {
            MessageUtil.sendOfflineMessage(Bukkit.getOfflinePlayer(staffId),"Failed to save restore data for plot #"
                                           +plot.getID()+" of plotbuild "+ plot.getPlotbuild().getName()+"."); 
        }
    }

}
