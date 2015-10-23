/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotSetInfo extends PlotBuildCommand {
    
    public PlotSetInfo(String... permissionNodes) {
        super(1, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": Defines the URL to the building guide.");
        setUsageDescription(" <URL> [name]: Adds the URL of the forum post with build instructions to the plotbuild (optional to set).");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        PlotBuild plotbuild = checkPlotBuild((Player) cs, 1, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        String url = args[0];
        if(!url.startsWith("http")) {
            url = "http://"+url;
        }
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            sendNoValidURL(cs);
            return;
        }
        plotbuild.setInfo(url);
        PluginData.saveData();
        sendPlotbuildInfoMessage(cs,plotbuild.getName());
    }

    private void sendPlotbuildInfoMessage(CommandSender cs,String name) {
        MessageUtil.sendInfoMessage(cs, "You added an info URL to plotbuild "+name
                                       +". Please verify that it is correct by using /plot help within a plot.");
    }

    private void sendNoValidURL(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "URL syntax incorrect.");
    }
    
}
