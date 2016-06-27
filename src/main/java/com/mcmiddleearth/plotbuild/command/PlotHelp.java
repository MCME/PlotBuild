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

import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import com.mcmiddleearth.plotbuild.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotHelp extends AbstractCommand{
    
    public PlotHelp(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": displays help about plotbuild commands.");
        setUsageDescription(" [command | page#]: Shows a description for [command]. If [command] is not specified a list of short descriptions for all plotbuild commands are shown. Point at a description with mouse cursor for detailed help.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Map <String, AbstractCommand> commands = ((PlotCommandExecutor)Bukkit.getPluginCommand("plot").getExecutor())
                                                           .getCommands();
        if(args.length>0 && !StringUtil.isPositiveInteger(args[0])){
            AbstractCommand command = commands.get(args[0]);
            if(command==null) {
                sendNoSuchCommandMessage(cs, args[0]);
            }
            else {
                String description = command.getUsageDescription();
                if(description==null){
                    description = command.getShortDescription();
                }
                if(description!=null){
                    sendDescriptionMessage(cs, args[0], description);
                }
                else {
                    sendNoDescriptionMessage(cs, args[0]);
                }
            }
        }
        else {
            Set<String> keys = commands.keySet();
            List<String> helpList = new ArrayList<>();
            List<String> tooltipList = new ArrayList<>();
            for(String key : keys) {
                String shortDescription = commands.get(key).getShortDescription();
                if(shortDescription!=null) {
                    helpList.add(ChatColor.DARK_AQUA+"/plot "+key+ChatColor.WHITE+shortDescription);
                    tooltipList.add(MessageUtil.hoverFormat("/plot "+key+commands.get(key).getUsageDescription(),":",true));
                }
            }
            int page=1;
            int maxPage=(helpList.size()-1)/10+1;
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
            sendHelpHeaderMessage(cs, page, maxPage);

            for(int i = helpList.size()-1-(page-1)*10; i >= 0 && i > helpList.size()-1-(page-1)*10-10; i--) {
                if(cs instanceof Player && tooltipList.get(i)!=null) {
                    MessageUtil.sendTooltipMessage((Player) cs, helpList.get(i),tooltipList.get(i));
                }
                else {
                    MessageUtil.sendNoPrefixInfoMessage(cs, helpList.get(i));
                }
            }
            if(cs instanceof Player && page<maxPage) {
                MessageUtil.sendClickableMessage((Player) cs, ChatColor.AQUA+">> Click for next page <<", "/plot help "+(page+1));
            }
        }
        sendManualMessage(cs);
    }

    private void sendHelpStartMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Help for PlotBuild plugin.");
    }

    private void sendHelpHeaderMessage(CommandSender cs, int page, int maxPage) {
        MessageUtil.sendInfoMessage(cs, "Help for PlotBuild plugin. [page "+page+"/"+maxPage+"]:");
    }

    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/plot "+arg+": There is no such command.");    
    }

    private void sendDescriptionMessage(CommandSender cs, String arg, String description) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/plot "+arg+description);
    }

    private void sendNoDescriptionMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/plot "+arg+": There is no help for this command.");
    }

   private void sendManualMessage(CommandSender cs) {
        //MessageUtil.sendNoPrefixInfoMessage(cs, "Manual for plotbuild plugin: ... .");
    }

}
