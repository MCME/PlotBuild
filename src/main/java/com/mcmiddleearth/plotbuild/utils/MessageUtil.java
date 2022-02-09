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
import com.mcmiddleearth.pluginutil.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            try {
                Object chatBaseComponent = com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("network.chat.IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, message);
                Object chatMessageType = com.mcmiddleearth.pluginutil.NMSUtil.invokeNMS("network.chat.ChatMessageType", "a", new Class[]{byte.class}, null, (byte)0);
                Constructor<?> titleConstructor = com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("network.protocol.game.PacketPlayOutChat").getConstructor(com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("network.chat.IChatBaseComponent"),
                        com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("network.chat.ChatMessageType"),
                        UUID.class);
                Object chatPacket = titleConstructor.newInstance(chatBaseComponent, chatMessageType, ((Player)sender).getUniqueId());
                NMSUtil.sendPacket((Player) sender, chatPacket);

                /*Object chatBaseComponent = com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, message);
                Object chatMessageType = com.mcmiddleearth.pluginutil.NMSUtil.invokeNMS("ChatMessageType", "a", new Class[]{byte.class}, null, (byte)0);
                Constructor<?> titleConstructor = com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("PacketPlayOutChat").getConstructor(com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("IChatBaseComponent"),
                        com.mcmiddleearth.pluginutil.NMSUtil.getNMSClass("ChatMessageType"),
                        UUID.class);
                Object chatPacket = titleConstructor.newInstance(chatBaseComponent, chatMessageType, ((Player)sender).getUniqueId());
                com.mcmiddleearth.pluginutil.NMSUtil.sendPacket((Player) sender, chatPacket);*/
                /*Object chatBaseComponent = NMSUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, message);
                Constructor<?> titleConstructor = NMSUtil.getNMSClass("PacketPlayOutChat").getConstructor(NMSUtil.getNMSClass("IChatBaseComponent"));
                Object chatPacket = titleConstructor.newInstance(chatBaseComponent);
                NMSUtil.sendPacket((Player)sender, chatPacket);*/
            } catch(Error | Exception e ) {
                Logger.getLogger(MessageUtil.class.getName()).log(Level.WARNING, "Error in Plotbuild plugin while accessing NMS class. This plugin version was not made for your server. Please look for an update. Plugin will use Bukkit.dispatchCommand to send '/tellraw ...' instead of directly sending message packets.");
                Logger.getLogger(com.mcmiddleearth.pluginutil.message.MessageUtil.class.getName()).log(Level.WARNING, null, e);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()+ " " + message);
            }    
        } else {
            sender.sendMessage(NOPREFIX + message);
        }
    }
    
    public static void sendClickableMessage(Player sender, String message, String onClickCommand) {
            sendNoPrefixRawMessage(sender,"{\"text\":\""+message+"\", "
                                    +"\"clickEvent\":{\"action\":\"suggest_command\","
                                    + "\"value\":\""+ onClickCommand +"\"}} ");
    }
        
    public static void sendTooltipMessage(Player sender, String message, String tooltip) {
            sendNoPrefixRawMessage(sender,"{\"text\":\""+message+"\", "
                                    +"\"hoverEvent\":{\"action\":\"show_text\","
                                    + "\"value\":\""+ tooltip +"\"}} ");
    }
        
    public static String hoverFormat(String hoverMessage,String headerSeparator, boolean header) {
        class MyScanner {
            private final Scanner scanner;
            public String currentToken=null;
            public MyScanner(String string) {
                scanner = new Scanner(string);
                scanner.useDelimiter(" ");
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                }
            }
            public String next() {
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                } else {
                    currentToken = null;
                }
                return currentToken;
            }
            public boolean hasCurrent() {
                return currentToken != null;
            }
            public boolean hasNext() {
                return scanner.hasNext();
            }
        }
        String result = (header?ChatColor.GOLD:ChatColor.YELLOW)+"";
        int separator = -1;
        if(header) {
            separator = hoverMessage.indexOf(headerSeparator);
            result = result.concat(hoverMessage.substring(0,separator+1));//+"\n");
        }
        MyScanner scanner = new MyScanner(hoverMessage.substring(separator+1));
        while (scanner.hasCurrent()) {
            String line = ChatColor.YELLOW+scanner.currentToken+" ";
            scanner.next();
            while(scanner.hasCurrent() && line.length()+scanner.currentToken.length()<40) {
                if(scanner.currentToken.equals("\n")) {
                    break;
                } else {
                    line = line.concat(scanner.currentToken+" ");
                    scanner.next();
                }
            }
            if(scanner.hasCurrent()) {
                //line = line.concat("\n");
                if(scanner.currentToken.equals("\n")) {
                    scanner.next();
                }
            }
            result = result.concat(line);
        }
        return result;
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

    public static String getNOPREFIX() {
        return NOPREFIX;
    }
}
