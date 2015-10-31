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
 * @author Ivan1pl
 */
public class PlotCurrent extends PlotBuildCommand {
    
    public PlotCurrent(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Sets the current plotbuild.");
        setUsageDescription(" <name>: Sets the plotbuild <name> as current plotbuild. That way it is not necessary to put the name of the plotbuild to work on in every command. The command /plot create sets the current plotbuild too.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
        if(plotbuild == null) {
            sendNoPlotbuildFoundMessage(cs);
            return;
        } 
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        sendCurrentPlotbuildSetMessage(cs);
        PluginData.setCurrentPlotbuild((Player) cs, plotbuild);
        PluginData.saveData();
    }
    
    private void sendCurrentPlotbuildSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Current plotbuild set.");
    }
    
}
