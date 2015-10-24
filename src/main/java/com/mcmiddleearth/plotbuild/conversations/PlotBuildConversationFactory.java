/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.conversations;

import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotBuildConversationFactory {
    
    private final ConversationFactory factory;
    
    public PlotBuildConversationFactory(Plugin plugin){
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new PlotBuildConversationPrefix())
                .withFirstPrompt(new QueryPrompt())
                .withTimeout(600);
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
    
}
