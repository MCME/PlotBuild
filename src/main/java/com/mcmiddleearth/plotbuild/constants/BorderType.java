/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.constants;

import lombok.Getter;

/**
 *
 * @author Ivan1pl
 */
public enum BorderType {
    GROUND  ("ground"),
    FLOAT   ("float"),
    NONE    ("none");
    
    @Getter
    private final String type;
    
    BorderType(String type) {
        this.type = type;
    }
    
    public static BorderType fromString(String value) {
        for(BorderType bt : BorderType.values()) {
            if(bt.getType().equals(value)) {
                return bt;
            }
        }
        return null;
    }
}
