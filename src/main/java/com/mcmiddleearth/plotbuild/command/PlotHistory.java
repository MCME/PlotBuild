/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.MessageUtil;
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
        List<String> history = plotbuild.getHistory();
        int page=1;
        int maxPage=(history.size()-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(args.length>0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendHistoryHeaderMessage(cs, plotbuild.getName(), page, maxPage);
        for(int i = (page-1)*10; i < history.size() && i < (page-1)*10+10; i++) {
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
