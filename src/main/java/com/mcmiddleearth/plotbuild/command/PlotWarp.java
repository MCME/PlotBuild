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

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotWarp extends PlotBuildCommand {
    
    public PlotWarp(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Teleports player to plotbuild.");
        setUsageDescription(" [name] [number]: Without a given number teleports a player to the first owned plot of the plotbuild name or the current plotbuild if no name is specified. If the player doesn't own a plot he is teleported to first plot of plotbuild. Otherwise to the plot with the given number.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 0, args);
        if(plotbuild == null) {
            return;
        }
        if(plotbuild.getPlots().isEmpty()) {
            sendNoPlotsErrorMessage(cs);
            return;
        }
        int plotID = 1;
        while(plotID < plotbuild.getPlots().size() 
                && plotbuild.getPlots().get(plotID-1).getState().equals(PlotState.REMOVED)) {
            plotID++;
        }
        Plot ownPlot = plotbuild.getPlot((Player) cs);
        if(ownPlot!=null) {
            plotID = ownPlot.getID();
        }
        if(args.length>1) {
            try {
                plotID = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e) {
                sendNoNumberErrorMessage(cs);
                return;
            }
            if(plotID<1 || plotID>plotbuild.getPlots().size()) {
                sendNoValidNumberErrorMessage(cs);
                return;
            }
        }
        Plot plot = plotbuild.getPlots().get(plotID-1); 
        Location loc;
        if(plot.getBorder().size()>0) {
            loc = plot.getBorder().get(0).getBlock().getRelative(0, 1, -2).getLocation();
        }
        else {
            loc = plot.getCorner1().getBlock().getRelative(0,1,0).getLocation();
        }
        for(int i=0; i<100; i++) {
            if(loc.getBlock().isEmpty() && loc.getBlock().getRelative(0,1,0).isEmpty()) {
                loc.setX(loc.getX() +0.5);
                loc.setY(loc.getY() + 0.5);
                ((Player) cs).teleport(loc);
                return;
            }
            loc = loc.getBlock().getRelative(randomStep(), 1, randomStep()).getLocation();
        }
        sendNoSavePlaceErrorMessage(cs);
    }

    private int randomStep() {
        if(Math.random()>0.5) {
            return 1;
        }
        else{
            return -1;
        }
    }

    private void sendNoPlotsErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can't teleport to that plotbuild as there are no plots.");
    }

    private void sendNoNumberErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to type in the number of the plot, for example /plot warp somename 1");
    }

    private void sendNoValidNumberErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No plot with that number.");
    }

    private void sendNoSavePlaceErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No safe place found near that plot.");
    }
    
}
