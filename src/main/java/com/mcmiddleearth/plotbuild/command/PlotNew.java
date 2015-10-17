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
public class PlotNew extends AbstractCommand {
    
    public PlotNew(String... permissionNodes) {
        super(0, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        //setting plotbuild
        PlotBuild plotbuild = null;
        if(args.length == 0){
            plotbuild = PluginData.getCurrentPlotbuild((Player) cs);
            if(plotbuild == null){
                sendNoCurrentPlotbuildMessage(cs);
                return;
            }
        }
        else{
            List <PlotBuild> plotbuilds = PluginData.getPlotbuildsList();
            for(PlotBuild searchPBuild : plotbuilds){
                if(searchPBuild.getName().equalsIgnoreCase(args[0])){
                    plotbuild = searchPBuild;
                    break;
                }
            }
            if(plotbuild == null){
                sendPlotbuildNotFoundMessage(cs);
                return;
            }
        }
        
        //check plot location and create plot
        Selection selection = PluginData.getCurrentSelection((Player) cs);
        if(selection.isValid()) {
            Plot newPlot=null;
            try {
                newPlot = new Plot(plotbuild, selection.getFirstPoint(),selection.getSecondPoint());
            } catch (InvalidPlotLocationException ex) {
                Logger.getLogger(PlotNew.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean success = plotbuild.getPlots().add(newPlot);
            if(success) {
                sendPlotCreatedMessage(cs);
            }
            else {
                sentPlotErrorMessage(cs);
            }

        }
        else {
            sendInvalidSelectionMessage(cs);
        }
                   
    }
        
    protected void sendPlotbuildNotFoundMessage(CommandSender cs){
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
    
}
