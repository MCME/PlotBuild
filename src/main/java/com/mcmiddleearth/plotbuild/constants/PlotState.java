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
package com.mcmiddleearth.plotbuild.constants;

import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public enum PlotState {
    
    UNCLAIMED (0),
    CLAIMED   (10),
    FINISHED  (11),
    REFUSED   (4),
    REMOVED  (13);
    
    @Getter
    private final int state;
    
    PlotState(int state){
        this.state = state;
    }
    
    public String getStateMessage() {
        switch(this) {
            case UNCLAIMED: return "Not started.";
            case CLAIMED: return "Unfinished.";
            case FINISHED: return "Waiting for staff feedback.";
            case REFUSED: return "Has been reviewed and needs improvements.";
            case REMOVED: return "Has been removed (accepted or deleted).";
            default: return "Illegal state.";
        }
    }
    
}
