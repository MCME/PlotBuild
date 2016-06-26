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
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotRefuse extends InsidePlotCommand {
    
    public PlotRefuse(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Markes a plot for improvement.");
        setUsageDescription(": When inside a finished plot can be used if there are still changes needed. Turns borders to yellow wool. ");
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
        if(!plot.refuse()) {
            sendNoSignPlaceMessage(cs);
        }
        for(UUID builder: plot.getOfflineOwners()) {
            if(!builder.equals(((Player)cs).getUniqueId())) {
                sendBuilderMessage(cs, Bukkit.getOfflinePlayer(builder), plot.getPlotbuild().getName(), plot.getID());
            }
        }
        sendRefuseMessgage(cs);
        plot.getPlotbuild().log(((Player) cs).getName()+" refused plot "+plot.getID()+".");
        PluginData.saveData();
    }

    private void sendRefuseMessgage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You refused this plot.");
    }
 
    private void sendBuilderMessage(CommandSender cs, OfflinePlayer builder, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, "Your plot #" + id + " of plotbuild " + name 
                                                     + " needs some improvements. " 
                                                     + "Check for notes on your build or ask "
                                                     + cs.getName()+" for instructions.");
    }

}
