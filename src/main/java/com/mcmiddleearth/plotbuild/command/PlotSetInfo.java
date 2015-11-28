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
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotSetInfo extends PlotBuildCommand {
    
    public PlotSetInfo(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Defines the URL to the building guide.");
        setUsageDescription(" <URL> [name]: Adds the URL of the forum post with build instructions to the plotbuild (optional to set).");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        String url = args[0];
        if(!url.startsWith("http")) {
            url = "http://"+url;
        }
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            sendNoValidURL(cs);
            return;
        }
        plotbuild.setInfo(url);
        PluginData.saveData();
        sendPlotbuildInfoMessage(cs,plotbuild.getName());
    }

    private void sendPlotbuildInfoMessage(CommandSender cs,String name) {
        MessageUtil.sendInfoMessage(cs, "You added an info URL to plotbuild "+name
                                       +". Please verify that it is correct by using /plot info within a plot.");
    }

    private void sendNoValidURL(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "URL syntax incorrect.");
    }
    
}
