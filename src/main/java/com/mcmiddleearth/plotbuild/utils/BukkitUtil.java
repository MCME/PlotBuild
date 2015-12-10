/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.utils;

import java.util.List;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class BukkitUtil {
    
    public static void removePlayerFromList(List<OfflinePlayer> list, OfflinePlayer player) {
        for(OfflinePlayer search: list) {
            if(search.getUniqueId().equals(player.getUniqueId())) {
                list.remove(search);
                return;
            }
        }
    }
}
