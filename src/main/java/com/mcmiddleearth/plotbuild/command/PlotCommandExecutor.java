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

import com.mcmiddleearth.plotbuild.constants.Permission;
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
        addCommandHandler("create", new PlotCreate(Permission.STAFF));
        addCommandHandler("setinfo", new PlotSetInfo(Permission.STAFF));
        addCommandHandler("sign", new PlotSign(Permission.STAFF));
        addCommandHandler("info", new PlotInfo(Permission.USER));
        addCommandHandler("help", new PlotHelp(Permission.USER));
        addCommandHandler("current", new PlotCurrent(Permission.STAFF));
        addCommandHandler("addstaff", new PlotAddstaff(Permission.STAFF));
        addCommandHandler("removestaff", new PlotRemoveStaff(Permission.STAFF));
        addCommandHandler("new", new PlotNew(Permission.STAFF));
        addCommandHandler("list", new PlotList(Permission.USER));
        addCommandHandler("claim", new PlotClaim(Permission.USER));
        addCommandHandler("assign", new PlotAssign(Permission.STAFF));
        addCommandHandler("invite", new PlotInvite(Permission.USER));
        addCommandHandler("leave", new PlotLeave(Permission.USER));
        addCommandHandler("finish", new PlotFinish(Permission.USER));
        addCommandHandler("unclaim", new PlotUnclaim(Permission.USER));
        addCommandHandler("accept", new PlotAccept(Permission.STAFF));
        addCommandHandler("refuse", new PlotRefuse(Permission.STAFF));
        addCommandHandler("clear", new PlotClear(Permission.STAFF));
        addCommandHandler("delete", new PlotDelete(Permission.STAFF));
        addCommandHandler("ban", new PlotBan(Permission.STAFF));
        addCommandHandler("unban", new PlotUnban(Permission.STAFF));
        addCommandHandler("history", new PlotHistory(Permission.STAFF));
        addCommandHandler("lock", new PlotLock(Permission.STAFF));
        addCommandHandler("unlock", new PlotUnlock(Permission.STAFF));
        addCommandHandler("end", new PlotEnd(Permission.STAFF));
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
