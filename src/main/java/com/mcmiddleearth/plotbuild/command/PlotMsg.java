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

import com.mcmiddleearth.plotbuild.constants.Permission;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotMsg extends InsidePlotCommand {
    
    public PlotMsg(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Sends a message to all owners of a plot.");
        setUsageDescription(" <player> <message>: Sends a message to all build team members of a plot even if they are offline. An offline player will get the message when he joins again.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(!(plot.isOwner((Player)cs) 
                || plot.getPlotbuild().isStaff((Player)cs) 
                || ((Player)cs).hasPermission(Permission.STAFF))) {
            sendNoPermError(cs);
            return;
        }
        String message = ChatColor.GOLD+"["+ChatColor.RED+((Player)cs).getName()
                       + ChatColor.GOLD+"] -> ["+ChatColor.RED+plot.getPlotbuild().getName()
                                                +", plot #"+plot.getID()
                                                +ChatColor.GOLD+"] "+ChatColor.AQUA;
        for(String arg : args) {
            message+=arg+" ";
        }
        for(UUID builder: plot.getOfflineOwners()) {
                MessageUtil.sendOfflineMessage(Bukkit.getOfflinePlayer(builder), message);
        }
        if(!plot.isOwner((Player)cs)) {
            MessageUtil.sendInfoMessage(cs, message);
        }
        plot.getPlotbuild().log(((Player) cs).getName()+" messaged to build team of plot #"+plot.getID()+": "+message);
    }

    private void sendNoPermError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Only plotbuild staff and build team members of a plot may use this command.");
    }
  }
