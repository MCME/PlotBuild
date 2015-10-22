/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.utils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Ivan1pl
 */
public class FileUtil {
    
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && FileUtil.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
    
}
