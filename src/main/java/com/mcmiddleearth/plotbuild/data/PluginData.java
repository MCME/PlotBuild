/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.data;

import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Location;
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
}
