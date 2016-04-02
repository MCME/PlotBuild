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
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Ivan1pl
 */
public class NewPlotPrompt extends FixedSetPrompt {
    
    private final String promptText;
    
    public NewPlotPrompt(String promptText) {
        super("yes", "no", "cancel");
        this.promptText = promptText;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String string) {
        PlotBuild plotbuild = (PlotBuild) cc.getSessionData("plotbuild");
        Selection selection = (Selection) cc.getSessionData("selection");
        CommandSender cs = (CommandSender) cc.getSessionData("cs");
        if (string.equalsIgnoreCase("yes")) {
            PlotNew.createPlot(plotbuild, selection, cs, true);
        } else if (string.equalsIgnoreCase("no")) {
            PlotNew.createPlot(plotbuild, selection, cs, false);
        } else {
            PlotNew.sendAbortMessage(cs);
        }
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return promptText + " " + formatFixedSet();
    }
    
}
