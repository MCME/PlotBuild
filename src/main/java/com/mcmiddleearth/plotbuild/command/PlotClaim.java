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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotClaim extends InsidePlotCommand {
    
    public PlotClaim(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getPlotbuild().isLocked()) {
            sendPlotbuildLockedMessage(cs);
            return;
        }
        if(plot.getOwners().contains((Player)cs)) {
            sendAlreadyOwnerMessage(cs);
            return;
        }
        if(plot.getState()!=PlotState.UNCLAIMED) {
            sendPlotAlreadyClaimedMessage(cs);
            return;
        }
        if(plot.getPlotbuild().isMember((Player) cs)) {
            sendAlreadyMemberMessage(cs);
            return;
        }
        plot.claim((Player) cs);
        sendPlotClaimedMessage(cs);
    }

    private void sendPlotClaimedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You claimed this plot.");
    }

    private void sendPlotAlreadyClaimedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was already claimed by an other player, you can ask him to invite you.");
    }

    private void sendAlreadyOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already owner of this plot.");
    }

    private void sendAlreadyMemberMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already owner of an other plot in this plotbuild.");
    }

    private void sendPlotbuildLockedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can not claim a plot at the moment as this plotbuild is locked. Try again later.");
    }
    
}
