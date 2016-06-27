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

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
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
        setShortDescription(": Creates a new plotbuild.");
        setUsageDescription(" <name> [bordertype] [height] [-p] [-3D]: Defines a new plotbuild called [name]. [bordertype] defines whether the plot border should be on the 'ground' (default), 'float' in air, at level of [height], or have no visible borders at all 'none'. The optional flag [-p] makes the plotbuild private, which makes it impossible to claim and invite players to a plot. Instead staff can assign players to a plot. With the [-3D] flag the plots are cuboid otherwise rectangular with full height of the map.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
    	//check plotbuild name
        String name = args[0];
        try{
            Integer.parseInt(args[0]);
            sendNumberNotAllowedMessage(cs);
        }
        catch(NumberFormatException e) {}
        List<PlotBuild> plotbuildsList = PluginData.getPlotbuildsList();
        for(PlotBuild plotbuild : plotbuildsList){
            if(plotbuild.getName().equalsIgnoreCase(name)){
                sendNameExistsErrorMessage(cs);
                return;
            }
        }
            
        //evaluate parameters
        BorderType borderType = BorderType.GROUND;
        boolean borderTypeDefined = false;
    	int borderHeight = ((Player)cs).getLocation().getBlockY();
    	boolean isPrivate = false;
        boolean isCuboid = false;
	for(int i = 1; i<args.length;i++){
            BorderType newBorderType = BorderType.fromString(args[i]);
            if(newBorderType!=null) {
                borderType = newBorderType;
                borderTypeDefined = true;
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
        if(!borderTypeDefined) {
            if(isCuboid) {
                borderType = BorderType.BOX;
            }
            else {
                borderType = BorderType.GROUND;
            }
        }
        if(!isCuboid && borderType == BorderType.BOX) {
            sendWrongBorderErrorMessage((Player) cs, isCuboid);
            return;
        }
        if(isCuboid && ((borderType == BorderType.GROUND) || (borderType == BorderType.FLOAT))) {
            sendWrongBorderErrorMessage((Player) cs, isCuboid);
            return;
        }
       
        //create new plotbuild
        PlotBuild newPlotbuild = new PlotBuild(name, borderType, borderHeight, isPrivate, isCuboid);
	boolean success = plotbuildsList.add(newPlotbuild);
        if(success){
            PluginData.setCurrentPlotbuild((Player) cs, newPlotbuild);
            newPlotbuild.addStaff((Player) cs);
            sendPlotbuildCreatedMessage(cs);
            newPlotbuild.log(((Player) cs).getName()+" created plotbuild "+newPlotbuild.getName()+".");
            PluginData.saveData();
        }
        else {
            sendPlotbuildCreateErrorMessage(cs);
        }
    }
    
    protected void sendNameExistsErrorMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "A Plotbuild with that name already exists.");
    }

    protected void sendPlotbuildCreateErrorMessage(CommandSender cs){
        MessageUtil.sendErrorMessage(cs, "There was an error. No plotbuild created.");
    }

    protected void sendPlotbuildCreatedMessage(CommandSender cs){
        MessageUtil.sendInfoMessage(cs, "Plotbuild created.");
    }   

    private void sendWrongBorderErrorMessage(Player player, boolean cuboid) {
        if(cuboid) {
            MessageUtil.sendErrorMessage(player, "Only border type cage (default) or none allowed with option -3D. No plotbuild created.");
        }
        else {
            MessageUtil.sendErrorMessage(player, "Border type cage is not allowed without option -3D. No plotbuild created.");
        }
    }

    private void sendNumberNotAllowedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs,"The name of a plotbuild must not be a number.");
    }
}
