/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class PlotBuild {
    
    @Getter
    private final String name;
    
    @Getter
    private final List <Plot> plots = new ArrayList <>();
    
    @Getter
    private final List <Player> staffList = new ArrayList <>();
    
    @Getter
    private final List <Player> bannedPlayers = new ArrayList <>();
    
    @Getter
    private final List <String> history = new ArrayList <>();
    
    @Getter
    @Setter
    private boolean locked = false;
    
    public PlotBuild(String name) {
        this.name = name;
    }
    
}
