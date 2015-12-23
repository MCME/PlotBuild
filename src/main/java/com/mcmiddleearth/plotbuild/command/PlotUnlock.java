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
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotUnlock extends PlotBuildCommand {
    
    public PlotUnlock(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Unlocks plotbuilds.");
        setUsageDescription(" [name]: Unlocks the plotbuild [name]. If [name] is not specified all plotbuilds are unlocked.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                plotbuild.setLocked(false);
                plotbuild.log(((Player) cs).getName()+" unlocked the plotbuild.");
            }
            sendAllUnlockBuilderMessages(cs);
            sendUnlockedAllMessage(cs);
        }
        else {
            PlotBuild plotbuild = PluginData.getPlotBuild(args[0]);
            if(plotbuild == null) {
                sendNoPlotbuildFoundMessage(cs);
                return;
            }
            plotbuild.setLocked(false);
            sendBuilderMessages(cs, plotbuild);
            plotbuild.log(((Player) cs).getName()+" unlocked the plotbuild.");
            sendUnlockedPlotbuild(cs,plotbuild.getName());
        }
        PluginData.saveData();
    }
    
    private void sendBuilderMessages(CommandSender cs, PlotBuild plotbuild) {
        Set<OfflinePlayer> builders = plotbuild.getBuilders();
        for(OfflinePlayer player : builders) {
            if(BukkitUtil.isOnline(player) && !BukkitUtil.isSame(player, (Player) cs)) {
                MessageUtil.sendInfoMessage(BukkitUtil.getPlayer(player), cs.getName() + "unlocked plotbuild "
                                               +plotbuild.getName()+".");
            }
        }
    }
    
    private void sendUnlockedAllMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You unlocked all plotbuilds.");
    }

    private void sendUnlockedPlotbuild(CommandSender cs, String name) {
        MessageUtil.sendInfoMessage(cs, "You unlocked the plotbuild "+name+".");
    }
    
    private void sendBuilderMessage(CommandSender cs, Player builder, String name) {
        MessageUtil.sendInfoMessage(builder, cs.getName()+" unlocked the plotbuild "+name+".");
    }

    private void sendAllUnlockBuilderMessages(CommandSender cs) {
        Set<OfflinePlayer> builders = PluginData.getBuilders();
        for(OfflinePlayer player : builders) {
            if(BukkitUtil.isOnline(player) && !BukkitUtil.isSame(player, (Player) cs)) {
                MessageUtil.sendInfoMessage(BukkitUtil.getPlayer(player), cs.getName() + "unlocked all plotbuilds.");
            }
        }
    }

}
