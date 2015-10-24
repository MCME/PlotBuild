/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.conversations;

import com.mcmiddleearth.plotbuild.command.PlotEnd;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QueryPrompt extends BooleanPrompt{

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, boolean answer) {
        if(answer) {
            PlotEnd.endPlotBuild((Player) cc.getSessionData("player"), 
                                   (PlotBuild) cc.getSessionData("plotbuild"),
                                   (Boolean) cc.getSessionData("keep"));
        }
        else {
            PlotEnd.sendAbordMessage((Player) cc.getSessionData("player"));
        }
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String answer) {
        if(answer.equalsIgnoreCase("true") || answer.equalsIgnoreCase("yes"))
            return acceptValidatedInput(cc, true);
        else
            return acceptValidatedInput(cc, false);
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return (String) cc.getSessionData("query");
    }
    
    @Override
    protected boolean isInputValid(ConversationContext context, String answer){
        return answer.equalsIgnoreCase("no") 
            || answer.equalsIgnoreCase("yes")
            || answer.equalsIgnoreCase("false")
            || answer.equalsIgnoreCase("true");
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput){
        return "Type in 'yes' or 'no'.";
    }
    
}
