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

import com.mcmiddleearth.plotbuild.constants.Permission;
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
public class PlotAddstaff extends PlotBuildCommand {
    
    public PlotAddstaff(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Adds a player to staff of a plotbuild.");
        setUsageDescription(" <player> [name]: For public projects that want to feature a plotbuild. This command can be used to give the (non-staff) project leader <player> access to the staff commands for the current plotbuild or the plotbuild called [name].");
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
        OfflinePlayer newStaff  = BukkitUtil.matchPlayer(args[0]);
        if(newStaff==null) {
            newStaff = Bukkit.getOfflinePlayer(args[0]);
        }
        if(newStaff.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(newStaff.isOnline() && !Bukkit.getPlayer(newStaff.getUniqueId()).hasPermission(Permission.USER)) {
            sendNoUserPerm(cs);
            return;
        }
        if(plotbuild.isStaff(newStaff)) {
            sendAlreadyStaffMessage(cs, newStaff, plotbuild.getName());
            return;
        }
        if(plotbuild.isBanned(newStaff)) {
            sendBannedMessage(cs, newStaff, plotbuild.getName());
            return;
        }
        plotbuild.addStaff(newStaff);
        sendAddStaffMessgage(cs, newStaff.getName(), plotbuild.getName());
        sendNewStaffPlayerMessage(cs, newStaff, plotbuild.getName());
        for(UUID staff: plotbuild.getOfflineStaffList()) {
            if(!staff.equals(((Player) cs).getUniqueId()) && !staff.equals(newStaff.getUniqueId())) {
                sendOtherStaffMessage(cs, Bukkit.getOfflinePlayer(staff), newStaff, plotbuild.getName());
            }
        }
        plotbuild.log(((Player) cs).getName()+" added "+newStaff.getName()+" to staff.");
        PluginData.saveData();
    }

    private void sendAddStaffMessgage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You added "+ name+" to staff of plotbuild "+plotbuild + ".");
    }

    private void sendNewStaffPlayerMessage(CommandSender cs, OfflinePlayer newStaff, String name) {
        MessageUtil.sendOfflineMessage(newStaff, "You were added to staff"
                                                     + " of plotbuild " + name 
                                                     + " by "+ cs.getName()+".");
    }

    private void sendOtherStaffMessage(CommandSender cs, OfflinePlayer staff, OfflinePlayer newStaff, String name) {
        MessageUtil.sendOfflineMessage(staff, cs.getName()+" added " + newStaff.getName()+ " to staff"
                                                     + " of plotbuild " + name +".");
    }

    private void sendAlreadyStaffMessage(CommandSender cs, OfflinePlayer newStaff, String name) {
        MessageUtil.sendErrorMessage(cs, newStaff.getName()+" is already staff of plotbuild "+name + ".");
    }

    private void sendBannedMessage(CommandSender cs, OfflinePlayer newStaff, String name) {
        MessageUtil.sendErrorMessage(cs, newStaff.getName()+" is banned from plotbuild "+name + ". You have to unban him first.");
    }

    private void sendNoUserPerm(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "The new staff you want to had, has no permission to use plotbuild.");
    }
    
}
