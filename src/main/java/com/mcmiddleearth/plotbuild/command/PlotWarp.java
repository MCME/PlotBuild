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
        setUsageDescription(" [plotbuild] [plot#]: Teleports a player to the plot with the given [plot#] of [plotbuild] or the current plotbuild if no [plotbuild] is specified. Without a given [plot#] teleports a player to the first owned plot. If the player doesn't own a plot he is teleported to first plot of [plotbuild].");
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
        if(plot.getPlotbuild().isCuboid()) {
            if(teleportToPlotCenter(plot, (Player) cs)){// || teleportToPlotPlace(plot, (Player) cs)) {
                return;
            }
        } else {
            if(teleportToRandomPlace(loc, (Player) cs)){// || teleportToPlotPlace(plot, (Player) cs)) {
                return;
            }
        }
        sendNoSavePlaceErrorMessage(cs);
    }
    
    private boolean teleportToPlotCenter(Plot plot, Player p) {
        Location cor1 = plot.getCorner1();
        Location cor2 = plot.getCorner2();
        Location loc = new Location(cor1.getWorld(),(cor1.getX()+cor2.getX())/2,
                                                           cor1.getY(),
                                                           (cor1.getZ()+cor2.getZ())/2);
        for(int j=0; j<30;j++) {
            for(int i=0;i<cor2.getY()-cor1.getY();i++) {
                Location search  = loc.getBlock().getRelative(0, i, 0).getLocation();
                if(isSave(search)) {
                    p.teleport(search);
                    return true;
                }
            }
            loc = loc.getBlock().getRelative(randomStep(), 0, randomStep()).getLocation();
        }
        return false;
    }
    
    private boolean teleportToPlotPlace(Plot plot, Player p) {
        for(int i=plot.getCorner1().getBlockX(); i<plot.getCorner2().getBlockX();i++) {
            for(int j=plot.getCorner1().getBlockZ();j<plot.getCorner2().getBlockZ();j++) {
                for(int k=plot.getCorner1().getBlockY();
                        k<(plot.getPlotbuild().isCuboid()?plot.getCorner2().getBlockY():
                                                          plot.getCorner2().getBlockY()+10);k++) {
                    Location search = new Location(plot.getCorner1().getWorld(),i,j,k);
                    if(isSave(search)) {
                        p.teleport(search);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean  teleportToRandomPlace(Location loc, Player p) {
        for(int i=0; i<100; i++) {
            if(isSave(loc)) {
                loc.setX(loc.getX() +0.5);
                loc.setY(loc.getY() + 0.5);
                p.teleport(loc);
                return true;
            }
            loc = loc.getBlock().getRelative(randomStep(), 1, randomStep()).getLocation();
        }
        return false;
    }
    
    private boolean isSave(Location loc) {
        return loc.getBlock().isEmpty() && loc.getBlock().getRelative(0,1,0).isEmpty();
                                        //&& !loc.getBlock().getRelative(0,-1,0).isEmpty();
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
