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
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotList extends PlotBuildCommand {
    
    public PlotList(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": Lists all plotbuilds or all plots of a plotbuild.");
        setUsageDescription(" [plotbuild] [#page#]: Without a specified plotbuild lists all running Plotbuilds along with number of unclaimed plots. With a given plotbuild displays all plots with their current states. Without a given page number the first page of list will be displayed.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length==0) {
            showPlotbuildList(cs,1);
        }
        else {
            try{
                int page = Integer.parseInt(args[0]);
                showPlotbuildList(cs,page);
            }
            catch(NumberFormatException e) {
                if(!(cs instanceof Player)) {
                    sendPlayerOnlyErrorMessage(cs);
                    return;
                }
                PlotBuild plotbuild = checkPlotBuild((Player) cs, 0, args);
                if(plotbuild == null) {
                    return;
                }
                if(args.length==1) {
                    showPlotList(cs, plotbuild, 1);
                }
                else {
                    int page = 1;
                    try{
                        page = Integer.parseInt(args[1]);
                    }
                    catch(NumberFormatException ex) {}
                    showPlotList(cs,plotbuild,page);
                }
            }
        }
    }
    
    private void showPlotbuildList(CommandSender cs, int page) {
        if(PluginData.getPlotbuildsList().isEmpty()) {
            MessageUtil.sendInfoMessage(cs, "There are no plotbuilds running. Check again later.");
        } else {
            List<String> messageList = new ArrayList<>();
            List<String> commandList = new ArrayList<>();
            for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
                if(cs instanceof Player) {
                    messageList.add(ChatColor.AQUA+MessageUtil.getNOPREFIX()+plotbuild.getName()
                                                            +" (unclaimed plots: " 
                                                            + Integer.toString(plotbuild.countUnclaimedPlots()) + ")");
                    commandList.add("/plot list "+plotbuild.getName());
                } else {
                    messageList.add(plotbuild.getName()
                                        +" (unclaimed plots: " 
                                        + Integer.toString(plotbuild.countUnclaimedPlots()) + ")");
                }
            }
            int maxPage=(messageList.size()-1)/10+1;
            if(maxPage<1) {
                maxPage = 1;
            }
            if(page>maxPage) {
                page = maxPage;
            }
            MessageUtil.sendInfoMessage(cs, "Running plotbuilds [page "+page+"/"+maxPage+"]:");
            for(int i = (page-1)*10; i < messageList.size() && i < (page-1)*10+10; i++) {
                if(cs instanceof Player) {
                    MessageUtil.sendClickableMessage((Player) cs, messageList.get(i),commandList.get(i));
                }
                else {
                    MessageUtil.sendNoPrefixInfoMessage(cs, messageList.get(i));
                }
            }
        }
    }

    private void showPlotList(CommandSender cs, PlotBuild plotbuild, int page) {
        List<String> plotList = new ArrayList<>();
        for(Plot plot : plotbuild.getPlots()) {
            plotList.add("{\"text\":\""+ChatColor.AQUA+MessageUtil.getNOPREFIX()
                                 +"Plot #"+plot.getID()+" "
                                 +MessageUtil.chatColorForPlotState(plot.getState())
                                 +plot.getState().getStateMessage()+"\", "
                          +"\"clickEvent\":{\"action\":\"run_command\","
                                      + "\"value\":\"/plot warp "+plotbuild.getName()+" "+
                                               + plot.getID() +"\"}}");
        }
        int maxPage=(plotList.size()-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendPlotlistHeaderMessage(cs, plotbuild.getName(), page, maxPage);
        for(int i = (page-1)*10; i < plotList.size() && i < (page-1)*10+10; i++) {
            sendPlotListEntryMessage(cs, plotList.get(i));
        }
        
    }

    private void sendPlotlistHeaderMessage(CommandSender cs, String name, int page, int maxPage) {
        MessageUtil.sendInfoMessage(cs, "Plots of plotbuild "+name+" [page "+page+"/"+maxPage+"]:");
    }

    private void sendPlotListEntryMessage(CommandSender cs, String get) {
        MessageUtil.sendNoPrefixRawMessage(cs, get);
    }
    
}
