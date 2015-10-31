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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Ivan1pl
 */
public class PlotCommandExecutor implements CommandExecutor {

    @Getter
    private final Map <String, AbstractCommand> commands = new LinkedHashMap <>();
    
    public PlotCommandExecutor() {
        addCommandHandler("create", new PlotCreate("plotbuild.staff"));
        addCommandHandler("setinfo", new PlotSetInfo("plotbuild.staff"));
        addCommandHandler("sign", new PlotSign("plotbuild.staff"));
        addCommandHandler("info", new PlotInfo("plotbuild.user"));
        addCommandHandler("help", new PlotHelp("plotbuild.user"));
        addCommandHandler("current", new PlotCurrent("plotbuild.staff"));
        addCommandHandler("addstaff", new PlotAddstaff("plotbuild.staff"));
        addCommandHandler("removestaff", new PlotRemoveStaff("plotbuild.staff"));
        addCommandHandler("new", new PlotNew("plotbuild.staff"));
        addCommandHandler("list", new PlotList("plotbuild.user"));
        addCommandHandler("claim", new PlotClaim("plotbuild.user"));
        addCommandHandler("assign", new PlotAssign("plotbuild.staff"));
        addCommandHandler("invite", new PlotInvite("plotbuild.user"));
        addCommandHandler("leave", new PlotLeave("plotbuild.user"));
        addCommandHandler("finish", new PlotFinish("plotbuild.user"));
        addCommandHandler("unclaim", new PlotUnclaim("plotbuild.user"));
        addCommandHandler("accept", new PlotAccept("plotbuild.staff"));
        addCommandHandler("refuse", new PlotRefuse("plotbuild.staff"));
        addCommandHandler("clear", new PlotClear("plotbuild.staff"));
        addCommandHandler("delete", new PlotDelete("plotbuild.staff"));
        addCommandHandler("ban", new PlotBan("plotbuild.staff"));
        addCommandHandler("unban", new PlotUnban("plotbuild.staff"));
        addCommandHandler("history", new PlotHistory("plotbuild.staff"));
        addCommandHandler("lock", new PlotLock("plotbuild.staff"));
        addCommandHandler("unlock", new PlotUnlock("plotbuild.staff"));
        addCommandHandler("end", new PlotEnd("plotbuild.staff"));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!string.equalsIgnoreCase("plot")) {
            return false;
        }
        if(strings == null || strings.length == 0) {
            sendNoSubcommandErrorMessage(cs);
            return true;
        }
        if(commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase()).handle(cs, Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }
    
    private void sendNoSubcommandErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You're missing subcommand name for this command.");
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Subcommand not found.");
    }
    
    private void addCommandHandler(String name, AbstractCommand handler) {
        commands.putIfAbsent(name, handler);
    }
    
}
