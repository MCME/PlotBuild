/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotEndEriol extends PlotBuildCommand{

        public PlotEndEriol(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Removes a plotbuild completely.");
        setUsageDescription(" <name>: Ends the plotbuild <name>, cleares and removes all plots that have not yet been accepted. With flag -k the plot states are kept. Deletes all plot state saves and history logs. ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild == null){
                sendNoPlotbuildFoundMessage(cs);
                return ;
            }
        
        PluginData.getPlotbuildsList().remove(plotbuild);
        //TODO remove save files.
        PluginData.saveData();
        sendEndPlotbuildMessage(cs,plotbuild.getName());
    }

    private void sendEndPlotbuildMessage(CommandSender cs,String name) {
        MessageUtil.sendInfoMessage(cs, "You removed plotbuild "+name+" from the map and deleted all saved data.");
    }

}
