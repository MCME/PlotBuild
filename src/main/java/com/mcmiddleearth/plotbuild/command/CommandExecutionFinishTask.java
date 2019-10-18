/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class CommandExecutionFinishTask extends BukkitRunnable {

    public String errorMessage = "";
    
    protected CommandSender cs;

    public CommandExecutionFinishTask(CommandSender cs) {
        this.cs = cs;
    }
    @Override
    public void run() {}
    
    public void sendErrorMessage() {
        if(!errorMessage.equals("")) {
            MessageUtil.sendErrorMessage(cs, errorMessage);
        }
    }
    
}
