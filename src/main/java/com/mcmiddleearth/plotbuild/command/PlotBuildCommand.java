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
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
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
