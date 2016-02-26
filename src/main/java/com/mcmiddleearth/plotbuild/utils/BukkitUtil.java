/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.utils;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class BukkitUtil {
    
    public static boolean isSame(OfflinePlayer player1, OfflinePlayer player2) {
        return player1.getUniqueId().equals(player2.getUniqueId());
    }
    
    public static void removePlayerFromList(List<OfflinePlayer> list, OfflinePlayer player) {
        OfflinePlayer found = null;
        for(OfflinePlayer search: list) {
            if(isSame(search, player)) {
                found = search;
            }
        }
        if(found!=null) {
             list.remove(found);
        }
    }
    
    public static boolean isPlayerInList(List<OfflinePlayer> list, OfflinePlayer player) {
        for(OfflinePlayer search : list) {
            if(isSame(search,player)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isOnline(UUID player) {
        return Bukkit.getPlayer(player)!=null;
    }
    
    public static Player getPlayer(OfflinePlayer player) {
        return Bukkit.getPlayer(player.getUniqueId());
    }
    
    public static Player matchPlayer(String name) {
        List<Player> matches = Bukkit.matchPlayer(name);
        if(matches.size()==1) {
            return matches.get(0);
        }
        else {
            return null;
        }
    }
    public static boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX()==loc2.getBlockX() 
            && loc1.getBlockY()==loc2.getBlockY() 
            && loc1.getBlockZ()==loc2.getBlockZ(); 
    }

}
