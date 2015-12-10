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
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Ivan1pl
 */
public class PlotBuild {
    
    @Getter
    private final String name;
    
    @Getter
    @Setter
    private String info;
    
    @Getter
    @Setter
    private List <Plot> plots = new ArrayList <>();
    
    @Setter
    private List <OfflinePlayer> staffList = new ArrayList <>();
    
    @Setter
    private List <OfflinePlayer> bannedPlayers = new ArrayList <>();
    
    @Getter
    @Setter
    private List <String> history = new ArrayList <>();
    
    @Getter
    @Setter
    private boolean locked = false;
    
    @Getter
    private final boolean priv;
    
    @Getter
    private final boolean cuboid;
    
    @Getter
    private final BorderType borderType;
    
    @Getter
    private final int borderHeight;
    
    public PlotBuild(String name, BorderType borderType, int borderHeight, boolean priv, boolean cuboid) {
        this.name = name;
        this.borderType = borderType;
        this.priv = priv;
        this.cuboid = cuboid;
        this.borderHeight = borderHeight;
    }
    
    public int countUnclaimedPlots() {
        int result = 0;
        for(Plot p : plots) {
            if(p.getState() == PlotState.UNCLAIMED) {
                result++;
            }
        }
        return result;
    }
    
    public int countUnfinishedPlots() {
        int result = 0;
        for(Plot p : plots) {
            if(p.getState() != PlotState.REMOVED) {
                result++;
            }
        }
        return result;
    }
    
    public boolean hasUnfinishedPlot(OfflinePlayer player) {
        for(Plot plot : plots) {
            if((plot.getState()==PlotState.CLAIMED || plot.getState()==PlotState.REFUSED) && plot.isOwner(player)) {
                return true;
            }
        }
        return false;
    }
     
    public boolean isBanned(OfflinePlayer player) {
        for(OfflinePlayer offPlayer : bannedPlayers) {
            if(player!=null && offPlayer.getUniqueId().equals((player.getUniqueId()))) {
                return true;
            }
        }
        return false;
    }
    
    /*public boolean isOfflineBanned(OfflinePlayer player) {
        return bannedPlayers.contains(player);
    }*/
    
    public List<OfflinePlayer> getOfflineBannedPlayers() {
        return bannedPlayers;
    }
    
    public void removeBan(OfflinePlayer player) {
        for(OfflinePlayer offPlayer : bannedPlayers) {
            if(player!=null && offPlayer.getUniqueId().equals((player.getUniqueId()))) {
                BukkitUtil.removePlayerFromList(bannedPlayers, player);
                return;
            }
        }
    }
    
    public boolean isStaff(OfflinePlayer player) {
        for(OfflinePlayer offPlayer : staffList) {
            //Player search = offPlayer.getPlayer();
            if(offPlayer!=null && offPlayer.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    /*public boolean isOfflineStaff(OfflinePlayer player) {
        return staffList.contains(player);
    }*/
    
    public void addStaff(OfflinePlayer player) {
        staffList.add(player);
    }
    
    public void removeStaff(OfflinePlayer player) {
        for(OfflinePlayer offPlayer : staffList) {
            if(player!=null && offPlayer.getUniqueId().equals((player.getUniqueId()))) {
                BukkitUtil.removePlayerFromList(staffList, player);
                return;
            }
        }
    }
    
    public List<OfflinePlayer> getOfflineStaffList() {
        return staffList;
    }
    public void addBanned(OfflinePlayer player) {
        bannedPlayers.add(player);
    }
    
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy'-'MM'-'dd' | 'HH':'mm ");

    public void log(String entry) {
        history.add(LocalDateTime.now().format(formatter)+entry);
    }
}
