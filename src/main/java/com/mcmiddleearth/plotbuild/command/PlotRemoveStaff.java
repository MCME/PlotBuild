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

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotRemoveStaff extends PlotBuildCommand{

    public PlotRemoveStaff(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Removes a player from staff of a plotbuild.");
        setUsageDescription(" <player> [name]: This command can be used to remove a (non-staff) project leader from the staff of current plotbuild or the plotbuild called [name].");
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
        OfflinePlayer removedStaff = Bukkit.getOfflinePlayer(args[0]);
        if(removedStaff.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(!plotbuild.isStaff(removedStaff)) {
            sendNotStaffMessage(cs, removedStaff, plotbuild.getName());
            return;
        }
        plotbuild.removeStaff(removedStaff);
        sendRemoveStaffMessage(cs, removedStaff.getName(), plotbuild.getName());
        if(removedStaff.getPlayer()!=cs) {
            sendRemovedStaffPlayerMessage(cs, removedStaff, plotbuild.getName());
        }
        for(OfflinePlayer staff: plotbuild.getOfflineStaffList()) {
            if(staff.getPlayer()!=(Player) cs && staff!=removedStaff) {
                sendOtherStaffMessage(cs, staff, removedStaff, plotbuild.getName());
            }
        }
        plotbuild.log(((Player) cs).getName()+" removed "+removedStaff.getName()+" from staff.");
        PluginData.saveData();
    }

    private void sendRemoveStaffMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You removed "+ name+" from staff of plotbuild "+plotbuild + ".");
    }

    private void sendRemovedStaffPlayerMessage(CommandSender cs, OfflinePlayer newStaff, String name) {
        MessageUtil.sendOfflineMessage(newStaff, "You were removed from staff"
                                                     + " of plotbuild " + name 
                                                     + " by "+ cs.getName()+".");
    }

    private void sendOtherStaffMessage(CommandSender cs, OfflinePlayer staff, OfflinePlayer newStaff, String name) {
        MessageUtil.sendOfflineMessage(staff, cs.getName()+" removed " + newStaff.getName()+ " from staff"
                                                     + " of plotbuild " + name +".");
    }

    private void sendNotStaffMessage(CommandSender cs, OfflinePlayer removedStaff, String name) {
        MessageUtil.sendErrorMessage(cs, removedStaff.getName()+" is not staff of plotbuild "+name + ".");
    }

}
