/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author Eriol_Eandur
 */
public class Selection {
    
    @Getter
    @Setter
    Location firstPoint;
    
    @Getter
    @Setter
    Location secondPoint;
    

    public boolean isValid(){
        return  firstPoint != null 
            && secondPoint != null
            && firstPoint.getWorld() == secondPoint.getWorld();
    }
}