/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.data.PluginData;
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
public class PlotCreate extends AbstractCommand {

    public PlotCreate(String... permissionNodes) {
        super(1, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
    	//check plotbuild name
        String name = args[0];
        List<PlotBuild> plotbuildsList = PluginData.getPlotbuildsList();
        for(PlotBuild plotbuild : plotbuildsList){
            if(plotbuild.getName().equalsIgnoreCase(name)){
                sendNameExistsErrorMessage(cs);
                return;
            }
        }
            
        //evaluate parameters
        BorderType borderType = BorderType.GROUND;
    	int borderHeight = ((Player)cs).getLocation().getBlockY();
    	boolean isPrivate = false;
	for(int i = 1; i<args.length;i++){
            BorderType newBorderType = BorderType.fromString(args[i]);
            if(newBorderType!=null)
                    borderType = newBorderType;
            if(args[i].equalsIgnoreCase("-p"))
                    isPrivate = true;
            try {
                    borderHeight = Integer.parseInt(args[i]);
            }
            catch(NumberFormatException e){}
        }
       
        //create new plotbuild
        PlotBuild newPlotbuild = new PlotBuild(name, borderType, borderHeight, isPrivate);
	boolean success = plotbuildsList.add(newPlotbuild);
        if(success){
            PluginData.setCurrentPlotbuild((Player) cs, newPlotbuild);
            newPlotbuild.getStaffList().add((Player) cs);
            sendPlotbuildCreatedMessage(cs);
            PluginData.saveData();
        }
        else {
            sendPlotbuildCreateErrorMessage(cs);
        }
    }
    
    protected void sendNameExistsErrorMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "A Plotbuild with the given name already exists.");
    }

    protected void sendPlotbuildCreateErrorMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "There was an error. No plotbuild created.");
    }

    protected void sendPlotbuildCreatedMessage(CommandSender cs){
        MessageUtil.sendInfoMessage(cs, "Plotbuild created.");
    }   
}
