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
import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotNew extends PlotBuildCommand {
    
    public PlotNew(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Creates a new plot.");
        setUsageDescription(" [name]: Creates a new plot to the current plotbuild, respectively to plotbuild [name] if specified. To select the area of the plot right/left click opposite corner. The y-values of the corners are only used for cuboid plot in plotbuilds with -3D flag.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 0, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        //check plot location and create plot
        Selection selection = PluginData.getCurrentSelection((Player) cs);
        if(selection.isValid()) {
            
            Plot intersectingPlot = PluginData.getIntersectingPlot(selection, plotbuild.isCuboid());
            if(intersectingPlot!=null) {
                sendPlotIntersectingMessage(cs, intersectingPlot.getPlotbuild().getName());
                return;
            }
            Plot newPlot=null;
            try {
                newPlot = new Plot(plotbuild, selection.getFirstPoint(),selection.getSecondPoint());
            } catch (InvalidPlotLocationException ex) {
                Logger.getLogger(PlotNew.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendPlotCreatedMessage(cs);
            newPlot.getPlotbuild().log(((Player) cs).getName()+" added plot "+newPlot.getID()+".");
            PluginData.saveData();
        }
        else {
            sendInvalidSelectionMessage(cs);
        }
                   
    }
        
    protected void sendInvalidSelectionMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "Invalid selection for a plot. Choose two corners with feather.");
    }   
    
    protected void sendPlotCreatedMessage(CommandSender cs){
        MessageUtil.sendInfoMessage(cs, "Plot created.");
    }   
    
    private void sendPlotIntersectingMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, "Your selection intersects with a plot from plotbuild "+name+". No plot created.");
    }
    
}
