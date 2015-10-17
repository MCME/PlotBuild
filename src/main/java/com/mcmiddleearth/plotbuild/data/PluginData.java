/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.data;

import com.mcmiddleearth.plotbuild.PlotBuildPlugin;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.ListUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PluginData {
    
    @Getter
    private static final List <PlotBuild> plotbuildsList = new ArrayList <>();
    
    private static final Map <Player, PlotBuild> currentPlotbuild = new LinkedHashMap <>();
    
    private static final Map <Player, Selection> selections = new LinkedHashMap <>();
    
    private static final File plotBuildDir = new File(PlotBuildPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "plotbuilds");
    
    static {
        if(!plotBuildDir.exists()) {
            plotBuildDir.mkdirs();
        }
    }

    public static void setCurrentPlotbuild(Player p, PlotBuild plotbuild) {
        currentPlotbuild.put(p, plotbuild);
    }
    
    public static PlotBuild getCurrentPlotbuild(Player p) {
        return currentPlotbuild.get(p);
    }
    
    public static Selection getCurrentSelection(Player p){
        Selection selection = selections.get(p);
        if(selection == null) {
            selection = new Selection();
            selections.put(p, selection);
        }
        return selection;
    }
    
    public static Plot getPlotAt(Location location) {
        for(PlotBuild plotbuild : plotbuildsList) {
            for(Plot plot : plotbuild.getPlots()) {
                if(plot.isInside(location)) {
                    return plot;
                }
            }
        }
        return null;
    }
    
    public static void saveData() {
        for(PlotBuild plotbuild : plotbuildsList) {
            try {
                savePlotBuild(plotbuild);
            } catch (IOException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void savePlotBuild(PlotBuild plotbuild) throws IOException {
        File plotBuildFile = new File(plotBuildDir, plotbuild.getName()+".pb");
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        plotBuildFile.createNewFile();
        plotDir.mkdir();
        if(plotBuildFile.exists() && plotDir.exists()) {
            FileWriter fw = new FileWriter(plotBuildFile.toString());
            PrintWriter writer = new PrintWriter(fw);
            writer.println(ListUtil.playerListToString(plotbuild.getStaffList()));
            writer.println(ListUtil.playerListToString(plotbuild.getBannedPlayers()));
            writer.println(plotbuild.isLocked());
            writer.println(plotbuild.isPriv());
            writer.println(plotbuild.getBorderType());
            writer.println(plotbuild.getBorderHeight());
            for(String entry : plotbuild.getHistory()) {
                writer.println(entry);
            }
            writer.close();
            for(int i = 0; i < plotbuild.getPlots().size(); ++i) {
                savePlot(plotbuild.getPlots().get(i), plotDir, i);
            }
        } else {
            throw new IOException();
        }
    }
    
    private static void savePlot(Plot plot, File plotDir, int i) throws IOException {
        File plotDataFile = new File(plotDir, Integer.toString(i) + ".p");
        File plotRestoreFile = new File(plotDir, Integer.toString(i) + ".r");
        boolean saveRestoreData = !plotRestoreFile.exists();
        plotDataFile.createNewFile();
        plotRestoreFile.createNewFile();
        if(plotDataFile.exists() && plotRestoreFile.exists()) {
            FileWriter fw = new FileWriter(plotDataFile.toString());
            PrintWriter writer = new PrintWriter(fw);
            writer.println(plot.getCorner1().getWorld().getName());
            writer.println(plot.getCorner1().getBlockX() + " "
                         + plot.getCorner1().getBlockY() + " "
                         + plot.getCorner1().getBlockZ());
            writer.println(plot.getCorner2().getWorld());
            writer.println(plot.getCorner2().getBlockX() + " "
                         + plot.getCorner2().getBlockY() + " "
                         + plot.getCorner2().getBlockZ());
            writer.println(ListUtil.playerListToString(plot.getOwners()));
            writer.println(plot.getState());
            for(Location l : plot.getBorder()) {
                writer.println(l.getWorld().getName());
                writer.println(l.getBlockX() + " "
                             + l.getBlockY() + " "
                             + l.getBlockZ());
            }
            writer.close();
            if(saveRestoreData) {
                savePlotRestoreData(plot, plotRestoreFile);
            }
        } else {
            throw new IOException();
        }
    }
    
    private static void savePlotRestoreData(Plot plot, File file) throws IOException {
        FileWriter fw = new FileWriter(file.toString());
        PrintWriter writer = new PrintWriter(fw);
        World world = plot.getCorner1().getWorld();
        writer.println(world.getName());
        for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
            for(int y = 0; y <= world.getMaxHeight(); ++y) {
                for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                    writer.println(world.getBlockAt(x, y, z).getType());
                    writer.println(world.getBlockAt(x, y, z).getState().getData().toItemStack().getDurability());
                }
            }
        }
    }
}
