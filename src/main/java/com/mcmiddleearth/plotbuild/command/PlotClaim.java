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
public class PlotClaim extends AbstractCommand {
    
    public PlotClaim(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = PluginData.getPlotAt(((Player) cs).getLocation());
        if(plot!=null) {
            if(plot.getState()==PlotState.UNCLAIMED) {
                plot.claim((Player) cs);
                sendPlotClaimedMessage(cs);
            }
            else {
                if(plot.getOwners().contains((Player)cs)) {
                    sendAlreadyOwnerMessage(cs);
                }
                else {
                    sendPlotAlreadyClaimedMessage(cs);
                }
            }
        }
        else {
            sendNotInPlotMessage(cs);
        }
    }

    private void sendPlotClaimedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You claimed this plot.");
    }

    private void sendPlotAlreadyClaimedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was already claimed by an other player, you can ask him to invite you.");
    }

    private void sendNotInPlotMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not in a plot.");
    }

    private void sendAlreadyOwnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already owner of this plot.");
    }
    
}
