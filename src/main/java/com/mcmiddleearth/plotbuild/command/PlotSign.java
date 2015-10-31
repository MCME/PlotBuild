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

import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotSign extends InsidePlotCommand{
    
    public PlotSign(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": renews the plot signs.");
        setUsageDescription(": Use when inside a plot, the wool blocks of the border and the plot signs are placed again.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Plot plot = checkInPlot((Player) cs);
        if(plot==null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plot.getPlotbuild())) {
            return;
        }
        plot.placeSigns();
        sendSignsPlacedMessage(cs);
    }

    private void sendSignsPlacedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Plot signs placed.");
    }
}
