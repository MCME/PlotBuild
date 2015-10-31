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
package com.mcmiddleearth.plotbuild.utils;

import com.mcmiddleearth.plotbuild.data.PluginData;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class MessageUtil {
    
    private static final String PREFIX   = "[PlotBuild] ";
    private static final String NOPREFIX = "    ";
    
    public static void sendErrorMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.AQUA + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendNoPrefixInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.AQUA + NOPREFIX + message);
        } else {
            sender.sendMessage(NOPREFIX + message);
        }
    }
    
    public static void sendOfflineMessage(OfflinePlayer offlinePlayer, String message) {
        Player player = offlinePlayer.getPlayer();
        if(player != null) {
            sendInfoMessage(player, message);
        }
        else {
            PluginData.addOfflineMessage(offlinePlayer, message);
        }
    }
    
    public static String getQueryPrefix() {
        return ChatColor.GOLD + PREFIX;
    }
}
