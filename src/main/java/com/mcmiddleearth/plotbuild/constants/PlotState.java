/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    ACCEPTED  (13);
    
    @Getter
    private final int state;
    
    PlotState(int state){
        this.state = state;
    }
    
}
