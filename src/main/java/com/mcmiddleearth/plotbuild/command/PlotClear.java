/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

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
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotClear extends InsidePlotCommand {
    
    public PlotClear(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        boolean unclaim=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-u")) {
            unclaim = true;
            sendClearAndUnclaimMessgage(cs);
        }
        else {
            sendClearMessage(cs);
        }
        try {
            plot.clear(unclaim);
        } catch (InvalidRestoreDataException ex) {
            Logger.getLogger(PlotClear.class.getName()).log(Level.SEVERE, null, ex);
            sendRestoreErrorMessage(cs);
        }
        PluginData.saveData();
    }

    private void sendClearAndUnclaimMessgage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared  and unclaimed this plot.");
    }

    private void sendClearMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You cleared this plot.");
    }
    
}
