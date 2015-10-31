/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.conversations;

import com.mcmiddleearth.plotbuild.command.PlotEnd;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotBuildConversationFactory implements ConversationAbandonedListener{
    
    private final ConversationFactory factory;
    
    public PlotBuildConversationFactory(Plugin plugin){
        factory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(new PlotBuildConversationPrefix())
                .withFirstPrompt(new QueryPrompt())
                .withTimeout(600)
                .addConversationAbandonedListener(this);
    }
    
    public void startQuery(Player player, String queryMessage, PlotBuild plotbuild, boolean keep) {
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData("query", queryMessage);
        context.setSessionData("player", player);
        context.setSessionData("plotbuild", plotbuild);
        context.setSessionData("keep", keep);
        context.setSessionData("anwer", false);
        conversation.begin();
    }
   
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationContext cc = abandonedEvent.getContext();
        if (abandonedEvent.gracefulExit() && (Boolean) cc.getSessionData("answer")) {
            PlotEnd.endPlotBuild((Player) cc.getSessionData("player"), 
                                   (PlotBuild) cc.getSessionData("plotbuild"),
                                   (Boolean) cc.getSessionData("keep"));
        }
        else {
            PlotEnd.sendAbordMessage((Player) cc.getSessionData("player"));
        }
    }
 
}
