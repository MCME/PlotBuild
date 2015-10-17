/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.constants.BorderType;
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
    
    @Getter
    private final boolean priv;
    
    @Getter
    private final boolean cuboid;
    
    @Getter
    private final BorderType borderType;
    
    @Getter
    private final int borderHeight;
    
    public PlotBuild(String name, BorderType borderType, int borderHeight, boolean priv, boolean cuboid) {
        this.name = name;
        this.borderType = borderType;
        this.priv = priv;
        this.cuboid = cuboid;
        this.borderHeight = borderHeight;
    }
    
}
