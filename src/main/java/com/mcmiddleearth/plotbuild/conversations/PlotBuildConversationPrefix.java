/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.conversations;

import com.mcmiddleearth.plotbuild.utils.MessageUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

/**
 *
 * @author Eriol_Eandur
 */
class PlotBuildConversationPrefix implements ConversationPrefix {

    @Override
    public String getPrefix(ConversationContext context) {
        return MessageUtil.getQueryPrefix();
    }
    
}
