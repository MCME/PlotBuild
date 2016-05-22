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

import com.mcmiddleearth.plotbuild.command.PlotNew;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Ivan1pl
 */
public class NewPlotConversationFactory implements ConversationAbandonedListener {
    
    private final ConversationFactory factory;
    
    public NewPlotConversationFactory(Plugin plugin){
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new PlotBuildConversationPrefix())
                .withFirstPrompt(new NewPlotPrompt("Keeping restore data for large plots may cause server lag or crash. Do you want to save restore data for this plot?"))
                .withTimeout(600)
                .addConversationAbandonedListener(this);
    }
    
    public void startConversation(Player cs, PlotBuild plotbuild, Selection selection) {
        Conversation conversation = factory.buildConversation(cs);
        ConversationContext context = conversation.getContext();
        context.setSessionData("plotbuild", plotbuild);
        context.setSessionData("selection", selection);
        context.setSessionData("cs", cs);
        conversation.begin();
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent cae) {
        if (!cae.gracefulExit()) {
            PlotNew.sendAbortMessage((CommandSender) cae.getContext().getSessionData("cs"));
        }
    }
    
}
