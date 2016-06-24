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

import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import com.mcmiddleearth.plotbuild.utils.StringUtil;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotHistory extends PlotBuildCommand {
    
    public PlotHistory(String... permissionNodes) {
        super(0, true, permissionNodes);
        setAdditionalPermissionsEnabled(true);
        setShortDescription(": action logs for a plotbuild");
        setUsageDescription(" [name] [#]: Displays all actions that happened in current plotbuild / plotbuild [name]. [#] is the page of the history list to be shown.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        int nameIndex = 0;
        int pageIndex = 1;
        if (args.length > 0 && StringUtil.isPositiveInteger(args[0])) {
            nameIndex = args.length + 1;
            pageIndex = 0;
        }
        PlotBuild plotbuild = checkPlotBuild((Player) cs, nameIndex, args);
        if(plotbuild == null) {
            return;
        }
        if(!hasPermissionsForPlotBuild((Player) cs, plotbuild)) {
            return;
        }
        List<String> history = plotbuild.getHistory();
        int page=1;
        int maxPage=(history.size()-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(args.length>pageIndex) {
            try {
                page = Integer.parseInt(args[pageIndex]);
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendHistoryHeaderMessage(cs, plotbuild.getName(), page, maxPage);
        
        for(int i = history.size()-1-(page-1)*10; i >= 0 && i > history.size()-1-(page-1)*10-10; i--) {
            sendHistoryEntryMessage(cs, history.get(i));
        }
        
    }

    private void sendHistoryHeaderMessage(CommandSender cs, String name, int page, int maxPage) {
        MessageUtil.sendInfoMessage(cs, "History of plotbuild "+name+" [page "+page+"/"+maxPage+"]:");
    }

    private void sendHistoryEntryMessage(CommandSender cs, String get) {
        MessageUtil.sendNoPrefixInfoMessage(cs, get);
    }
    
}
