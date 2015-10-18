/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotDelete extends InsidePlotCommand {
    
    public PlotDelete(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
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
        boolean keep=false;
        if(args.length > 0 && args[0].equalsIgnoreCase("-k")) {
            keep = true;
            sendDeleteAndKeepMessage(cs);
        }
        else {
            sendDeleteMessage(cs);
        }
        plot.delete(keep);
        PluginData.saveData();
    }

    private void sendDeleteMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and cleared the changes within.");
    }

    private void sendDeleteAndKeepMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You deleted this plot and kept the changes within.");
    }
    
}
