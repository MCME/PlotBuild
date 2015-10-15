/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.exceptions;

/**
 *
 * @author Ivan1pl
 */
public class InvalidPlotLocationException extends Exception {
    
    public InvalidPlotLocationException() {
        super();
    }
    
    public InvalidPlotLocationException(String message) {
        super(message);
    }
    
    public InvalidPlotLocationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidPlotLocationException(Throwable cause) {
        super(cause);
    }
    
}
