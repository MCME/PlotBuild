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
import org.bukkit.entity.Player;

/**
 *
 * @author Lars
 */
public abstract class PlotBuildCommand extends AbstractCommand{

    public PlotBuildCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        super (minArgs, playerOnly, permissionNodes);
    }

    protected PlotBuild checkPlotBuild(Player player, int nameIndex, String... args) {
        //setting plotbuild
        PlotBuild plotbuild = null;
        if(args.length <= nameIndex){
            plotbuild = PluginData.getCurrentPlotbuild(player);
            if(plotbuild == null){
                sendNoCurrentPlotbuildMessage(player);
                return null;
            }
        }
        else{
            plotbuild=PluginData.getPlotBuild(args[nameIndex]);
            if(plotbuild == null){
                sendNoPlotbuildFoundMessage(player);
                return null;
            }
        }
        return plotbuild;
    }
 
    protected void sendNoCurrentPlotbuildMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "No current plotbuild.");
    }   


}   
