/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import java.util.List;
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
        boolean isCuboid = false;
	for(int i = 1; i<args.length;i++){
            BorderType newBorderType = BorderType.fromString(args[i]);
            if(newBorderType!=null) {
                borderType = newBorderType;
            }
            if(args[i].equalsIgnoreCase("-p")) {
                isPrivate = true;
            }
            if(args[i].equalsIgnoreCase("-3D")) {
                isCuboid = true;
            }
            try {
                borderHeight = Integer.parseInt(args[i]);
            }
            catch(NumberFormatException e){}
        }
       
        //create new plotbuild
        PlotBuild newPlotbuild = new PlotBuild(name, borderType, borderHeight, isPrivate, isCuboid);
	boolean success = plotbuildsList.add(newPlotbuild);
        if(success){
            PluginData.setCurrentPlotbuild((Player) cs, newPlotbuild);
            sendPlotbuildCreatedMessage(cs);
        }
        else {
            sendPlotbuildCreateErrorMessage(cs);
        }
    }
    
    protected void sendNameExistsErrorMessage(CommandSender cs){
        cs.sendMessage("A Plotbuild with the given name already exists.");
    }

    protected void sendPlotbuildCreateErrorMessage(CommandSender cs){
        cs.sendMessage("There was an error. No plotbuild created.");
    }

    protected void sendPlotbuildCreatedMessage(CommandSender cs){
        cs.sendMessage("Plotbuild created.");
    }   
}
