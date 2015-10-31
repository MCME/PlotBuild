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
