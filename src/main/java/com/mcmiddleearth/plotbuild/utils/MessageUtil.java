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

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import static net.minecraft.server.v1_8_R3.Block.p;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
    
    public static void sendNoPrefixRawMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            //Packet packet = new PacketPlayOutChat(ChatSerializer.a(message));
            //((CraftPlayer) sender).getHandle().playerConnection.sendPacket(packet);
            //((Player)sender).sendRawMessage(ChatColor.AQUA + NOPREFIX + message);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+ ((Player)sender).getName()+" "+message);
        } else {
            sender.sendMessage(NOPREFIX + message);
        }
    }
    
    public static void sendOfflineMessage(OfflinePlayer offlinePlayer, String message) {
        Player player=null;
        for(Player search : Bukkit.getOnlinePlayers()) {
            if(search.getUniqueId().equals(offlinePlayer.getUniqueId())) {
                player = search;
                break;
            }
        }
        if(player!=null) {  
            sendInfoMessage(player, message);
        }
        else {
            PluginData.addOfflineMessage(offlinePlayer, message);
        }
    }
    
    public static String getQueryPrefix() {
        return ChatColor.GOLD + PREFIX;
    }
    
    public static ChatColor chatColorForPlotState(PlotState state) {
        switch(state) {
            case UNCLAIMED: 
                return ChatColor.WHITE;
            case CLAIMED:
                return ChatColor.LIGHT_PURPLE;
            case FINISHED:
                return ChatColor.BLUE;
            case REFUSED:
                return ChatColor.YELLOW;
            default:
                return ChatColor.DARK_GRAY;
        }
    }
}
