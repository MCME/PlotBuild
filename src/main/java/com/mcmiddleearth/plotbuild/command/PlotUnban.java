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
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
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
public class PlotUnban extends PlotBuildCommand {
    
    public PlotUnban(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Unbans a player from a plotbuild.");
        setUsageDescription(" <player> [name]: Unbans player from current plotbuild respectively from plotbuild [name].");
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
        OfflinePlayer banned = BukkitUtil.matchPlayer(args[0]);
        if(!plotbuild.getOfflineBannedPlayers().contains(banned.getUniqueId())) {
            banned = null;
            for(UUID search : plotbuild.getOfflineBannedPlayers()) {
                if(Bukkit.getOfflinePlayer(search).getName().equals(args[0])) {
                    banned = Bukkit.getOfflinePlayer(search);
                    break;
                }
            }
        }
        if(banned==null) {
            sendNotBannedMessage(cs, args[0], plotbuild.getName());
            return;
        }
        plotbuild.removeBan(banned);
        sendUnbannedMessage(cs,banned.getName(),plotbuild.getName());
        sendUnbannedPlayerMessage(cs, banned, plotbuild.getName());
        plotbuild.log(((Player) cs).getName()+" unbanned "+banned.getName()+".");
        PluginData.saveData();
    }

    private void sendUnbannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You unbanned "+ name+" from plotbuild "+plotbuild + ".");
    }

    private void sendNotBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendErrorMessage(cs, name+" is not banned from plotbuild "+plotbuild + ".");
    }
    
    private void sendUnbannedPlayerMessage(CommandSender cs, OfflinePlayer banned, String name) {
        MessageUtil.sendOfflineMessage(banned, "Your ban"
                                                     + " from plotbuild " + name 
                                                     + " has been revoked by "+ cs.getName()+".");
    }
}
