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

/**
 *
 * @author Ivan1pl
 */
public class PlotList extends AbstractCommand {
    
    public PlotList(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": Lists all plotbuilds.");
        setUsageDescription(": Lists all running Plotbuilds along with number of unclaimed plots.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        MessageUtil.sendInfoMessage(cs, "Running plotbuilds:");
        if(PluginData.getPlotbuildsList().isEmpty()) {
            MessageUtil.sendNoPrefixInfoMessage(cs, "There are no plotbuilds running. Check again later.");
        } else {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                MessageUtil.sendNoPrefixInfoMessage(cs, plotbuild.getName() + " (unclaimed plots: " +
                        Integer.toString(plotbuild.countUnclaimedPlots()) + ")");
            }
        }
    }
    
}
