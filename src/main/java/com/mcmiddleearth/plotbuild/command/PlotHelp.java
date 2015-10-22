/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Lars
 */
public class PlotHelp extends AbstractCommand{
    
    public PlotHelp(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": displays help about plotbuild commands.");
        setUsageDescription(" [command]: Shows a description for [command]. If [command] is not specified a list of short descriptions for all plotbuild commands is shown.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        sendHelpStartMessage(cs);
        Map <String, AbstractCommand> commands = ((PlotCommandExecutor)Bukkit.getPluginCommand("plot").getExecutor())
                                                           .getCommands();
        if(args.length>0){
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
            for(String key : keys) {
                String description = commands.get(key).getShortDescription();
                if(description!=null){
                    sendDescriptionMessage(cs, key, description);
                }
                else {
                    sendNoDescriptionMessage(cs, key);
                }
            }
        }
        sendManualMessage(cs);
    }

    private void sendHelpStartMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Help for plotbuild plugin.");
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
