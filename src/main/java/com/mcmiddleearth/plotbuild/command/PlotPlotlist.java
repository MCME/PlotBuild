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
 * @author Ivan1pl, Eriol_Eandur
 */
public class PlotPlotlist extends PlotBuildCommand {
    
    public PlotPlotlist(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": plot list for a plotbuild");
        setUsageDescription(": Displays all plots of current plotbuild with their current states.");
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
        List<String> plotList = new ArrayList<>();
        for(Plot plot : plotbuild.getPlots()) {
            plotList.add("{ text:\"Plot #"+plot.getID()+" "
                                 +MessageUtil.chatColorForPlotState(plot.getState())
                                 +plot.getState().getStateMessage()+"\", "
                          +"clickEvent:{ action:run_command,"
                                      + "value:\"/plot warp "+plotbuild.getName()+" "+
                                               + plot.getID() +"\"}}");
        }
        int page=1;
        int maxPage=(plotList.size()-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(args.length>1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendPlotlistHeaderMessage(cs, plotbuild.getName(), page, maxPage);
        for(int i = (page-1)*10; i < plotList.size() && i < (page-1)*10+10; i++) {
        //for(int i = plotList.size()-1-(page-1)*10; i >= 0 && i > plotList.size()-1-(page-1)*10-10; i--) {
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
