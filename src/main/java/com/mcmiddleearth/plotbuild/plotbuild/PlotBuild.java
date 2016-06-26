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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    private List <UUID> staffList = new ArrayList <>();
    
    @Setter
    private List <UUID> bannedPlayers = new ArrayList <>();
    
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
    
    public Plot getPlot(OfflinePlayer player) {
        for(Plot plot : plots) {
            if((plot.getState()==PlotState.CLAIMED || plot.getState()==PlotState.REFUSED) && plot.isOwner(player)) {
                return plot;
            }
        }
        return null;
    }
     
    public boolean isBanned(OfflinePlayer player) {
        for(UUID offPlayer : bannedPlayers) {
            if(player!=null && offPlayer.equals((player.getUniqueId()))) {
                return true;
            }
        }
        return false;
    }
    
    public List<UUID> getOfflineBannedPlayers() {
        return bannedPlayers;
    }
    
    public void removeBan(OfflinePlayer player) {
        UUID found = null;
        for(UUID offPlayer : bannedPlayers) {
            if(player!=null && offPlayer.equals((player.getUniqueId()))) {
                found = offPlayer;
                break;
            }
        }
        if(found !=null) {
            bannedPlayers.remove(found);
        }
    }
    
    public boolean isStaff(OfflinePlayer player) {
        for(UUID offPlayer : staffList) {
            if(player!=null && offPlayer.equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    public void addStaff(OfflinePlayer player) {
        staffList.add(player.getUniqueId());
    }
    
    public void removeStaff(OfflinePlayer player) {
        UUID found = null;
        for(UUID offPlayer : staffList) {
            if(player!=null && offPlayer.equals((player.getUniqueId()))) {
                found = offPlayer;
                break;
            }
        }
        if(found!=null) {
            staffList.remove(found);
        }
    }
    
    public List<UUID> getOfflineStaffList() {
        return staffList;
    }
    
    public void addBanned(OfflinePlayer player) {
        bannedPlayers.add(player.getUniqueId());
    }
    
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy'-'MM'-'dd' | 'HH':'mm ");

    public void log(String entry) {
        history.add(LocalDateTime.now().format(formatter)+entry);
    }
    
    public Set<UUID> getBuilders() {
        Set<UUID> builders = new HashSet<>();
        for(Plot plot : getPlots()) {
            if(plot.getState()!=PlotState.REMOVED) {
                List<UUID> owners = plot.getOfflineOwners();
                builders.addAll(owners);
            }
        }
        return builders;
    }
    
    
}
