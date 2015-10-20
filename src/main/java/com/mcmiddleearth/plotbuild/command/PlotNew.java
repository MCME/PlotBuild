/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.List;
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
            boolean success = plotbuild.getPlots().add(newPlot);
            if(success) {
                sendPlotCreatedMessage(cs);
                newPlot.getPlotbuild().log(((Player) cs).getName()+" added plot "+newPlot.getID()+".");
                PluginData.saveData();
            }
            else {
                sentPlotErrorMessage(cs);
            }

        }
        else {
            sendInvalidSelectionMessage(cs);
        }
                   
    }
        
    protected void sendNoPlotbuildFoundMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "No plotbuild with this name.");
    }   

    protected void sendNoCurrentPlotbuildMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "No current plotbuild.");
    }   

    protected void sendInvalidSelectionMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "Invalid selection for a plot. Choose two corners with feather.");
    }   
    
    protected void sendPlotCreatedMessage(CommandSender cs){
        MessageUtil.sendInfoMessage(cs, "Plot created.");
    }   
    
    protected void sentPlotErrorMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "There was an error. No plot created.");
    }   

    private void sendPlotIntersectingMessage(CommandSender cs, String name) {
        MessageUtil.sendErrorMessage(cs, "Your selection intersects with a plot from plotbuild "+name+". No plot created.");
    }
    
}
