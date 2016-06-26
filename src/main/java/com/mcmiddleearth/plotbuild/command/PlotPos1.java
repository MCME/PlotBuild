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
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotPos1 extends PlotBuildCommand {

    public PlotPos1(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Sets first point of selection to given coordinates.");
        setUsageDescription(" <x> <y> <z>: This command can be used to set first point of selection to coordinates x, y, z.");
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
        
        Selection selection = PluginData.getCurrentSelection((Player) cs);
        
        int x, y, z;
        try {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            z = Integer.parseInt(args[2]);

            selection.setFirstPoint(new Location(((Player) cs).getWorld(), x, y, z));
            sendFirstPointSetMessage((Player) cs, selection);
        } catch(NumberFormatException e) {
            sendInvalidNumberFormatErrorMessage((Player) cs, args[0], args[1], args[2]);
        }
    }

    private void sendFirstPointSetMessage(Player player, Selection sel) {
        String message = "First point set";
        if(sel.isValid()) {
            message += " (area: " + Integer.toString(sel.getArea()) + " blocks, volume: "
                    + Integer.toString(sel.getVolume()) + " blocks)";
        }
        message += ".";
        MessageUtil.sendInfoMessage(player, message);
    }
    
    private void sendInvalidNumberFormatErrorMessage(Player player, String x, String y, String z) {
        MessageUtil.sendErrorMessage(player, "Invalid coordinates: " + x + ", " + y + ", " + z + ".");
    }

}
