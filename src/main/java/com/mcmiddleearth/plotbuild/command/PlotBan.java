/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotBan extends PlotBuildCommand {
    
    public PlotBan(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Bans a player from a plotbuild.");
        setUsageDescription(" <player> [name]: Restricts player from claiming any further plots in the plotbuild [name]. If [name] is not specified, current plotbuild is used. Removes the player from all plots. Does not clear plots.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String logMessage = "";
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        OfflinePlayer banned = Bukkit.getOfflinePlayer(args[0]);
        if(banned.getLastPlayed()==0) {
            sendPlayerNotFoundMessage(cs);
            return;
        }
        if(plotbuild.getBannedPlayers().contains(banned)) {
            sendPlayerAlreadyBannedMessage(cs, banned.getName(), plotbuild.getName());
            return;
        }
        for(Plot plot : plotbuild.getPlots()) {
            if(plot.getState()!=PlotState.REMOVED && plot.getOwners().contains(banned)) {
                if(plot.getOwners().size()==1) {
                    try {
                        plot.unclaim();
                    } catch (InvalidRestoreDataException ex) {
                        Logger.getLogger(PlotDelete.class.getName()).log(Level.SEVERE, null, ex);
                        sendRestoreErrorMessage(cs);
                        logMessage = " There was an error during clearing of a plot.";
                    }
                }
                else {
                    plot.leave(banned);
                    for(OfflinePlayer builder: plot.getOwners()) {
                        if(builder.getPlayer()!=cs) {
                            sendOtherBuilderMessage(cs, builder, banned, plot.getPlotbuild().getName(), plot.getID());
                }
            }
                }
            }
        }
        plotbuild.getBannedPlayers().add(banned);
        if(plotbuild.getStaffList().contains(banned)) {
            plotbuild.getStaffList().remove(banned);
            for(OfflinePlayer staff: plotbuild.getStaffList()) {
                if(staff.getPlayer()!=(Player) cs) {
                    sendOtherStaffMessage(cs, staff, banned, plotbuild.getName());
                }
            }
        }
        sendBannedMessage(cs,banned.getName(), plotbuild.getName());
        sendBannedPlayerMessage(cs, banned, plotbuild.getName());
        plotbuild.log(((Player) cs).getName()+" banned "+banned.getName()+"."+logMessage);
        PluginData.saveData();
    }

    private void sendBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, "You banned "+ name+" from plotbuild "+plotbuild + ".");
    }
    
    private void sendPlayerAlreadyBannedMessage(CommandSender cs, String name, String plotbuild) {
        MessageUtil.sendInfoMessage(cs, name+" is already banned from plotbuild "+plotbuild + ".");
    }

    private void sendBannedPlayerMessage(CommandSender cs, OfflinePlayer banned, String name) {
        MessageUtil.sendOfflineMessage(banned, "You have been excluded"
                                                     + " from plotbuild " + name 
                                                     + " by "+ cs.getName()+". Please ask staff for more info.");
    }

    private void sendOtherBuilderMessage(CommandSender cs, OfflinePlayer builder, OfflinePlayer banned, String name, int id) {
        MessageUtil.sendOfflineMessage(builder, banned.getName()+" was removed from the build team of plot #"+id
                                                     + " of plotbuild " + name 
                                                     + " as he was banned by "+ cs.getName()+".");
    }

    private void sendOtherStaffMessage(CommandSender cs, OfflinePlayer staff, OfflinePlayer banned, String name) {
        MessageUtil.sendOfflineMessage(staff, cs.getName()+" removed " + banned.getName()+ " from staff"
                                                     + " of plotbuild " + name +" and banned him.");
    }

}
