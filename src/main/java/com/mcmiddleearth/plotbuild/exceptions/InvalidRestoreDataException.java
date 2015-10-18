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
public class InvalidRestoreDataException extends Exception {
    
    public InvalidRestoreDataException() {
        super();
    }
    
    public InvalidRestoreDataException(String message) {
        super(message);
    }
    
    public InvalidRestoreDataException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidRestoreDataException(Throwable cause) {
        super(cause);
    }
    
}
