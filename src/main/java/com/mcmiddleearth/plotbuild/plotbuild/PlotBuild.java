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
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * @author Ivan1pl
 */
public class PlotBuild {
    
    private final String name;
    private String info;
    
    private List <Plot> plots = new ArrayList <>();
    private List <UUID> staffList = new ArrayList <>();
    private List <UUID> bannedPlayers = new ArrayList <>();
    private List <String> history = new ArrayList <>();
    
    private boolean locked = false;
    
    private final boolean priv;
    private final boolean cuboid;
    
    private final BorderType borderType;
    private final int borderHeight;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy'-'MM'-'dd' | 'HH':'mm ");

    public PlotBuild(String name, BorderType borderType, int borderHeight, boolean priv, boolean cuboid) {
        this.name = name;
        this.borderType = borderType;
        this.priv = priv;
        this.cuboid = cuboid;
        this.borderHeight = borderHeight;
    }
    
    public boolean isSaveInProgress() {
        for(Plot p: plots) {
            if(p.isSaveInProgress()) {
                return true;
            }
        }
        return false;
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

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Plot> getPlots() {
        return plots;
    }

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
    }

    public void setStaffList(List<UUID> staffList) {
        this.staffList = staffList;
    }

    public void setBannedPlayers(List<UUID> bannedPlayers) {
        this.bannedPlayers = bannedPlayers;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isPriv() {
        return priv;
    }

    public boolean isCuboid() {
        return cuboid;
    }

    public BorderType getBorderType() {
        return borderType;
    }

    public int getBorderHeight() {
        return borderHeight;
    }
}
