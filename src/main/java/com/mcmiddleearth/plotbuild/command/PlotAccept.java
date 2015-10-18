/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotAccept extends InsidePlotCommand {
    
    public PlotAccept(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(plot.getState()!=PlotState.FINISHED) {
            sendNotFinishedMessage(cs);
            return;
        }
        try {
            plot.accept();
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotAccept.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendAcceptMessage(cs);
        PluginData.saveData();
    }

    private void sendAcceptMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You accepted this plot.");
    }

    private void sendNotFinishedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This plot was not marked as finished. You can remove it with /plot delete -k.");
    }
    
}
