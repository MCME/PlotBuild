/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import java.io.IOException;

/**
 *
 * @author Eriol_Eandur
 */
public interface PlotFormat {
    
    public void save(Plot plot) throws IOException;
    
    public void load(Plot plot) throws IOException, InvalidRestoreDataException;
    
}
