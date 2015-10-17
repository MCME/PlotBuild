/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Ivan1pl
 */
public class ListUtil {
    
    public static String playerListToString(List <OfflinePlayer> players) {
        ArrayList <String> uuids = new ArrayList<>();
        for (OfflinePlayer player : players) {
            uuids.add(player.getUniqueId().toString());
        }
        return Joiner.on(';').join(uuids);
    }
    
    public static List <OfflinePlayer> playerListFromString(String uuids) {
        ArrayList <OfflinePlayer> players = new ArrayList<>();
        if(uuids.length() > 0) {
            for(String uuid : Splitter.on(';').split(uuids)) {
                players.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
            }
        }
        return players;
    }
    
    public static List <Integer> integersFromString(String string, char delim) {
        List <String> strList = Splitter.on(delim).splitToList(string);
        ArrayList <Integer> list = new ArrayList<>();
        for(String s : strList) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }
    
}
