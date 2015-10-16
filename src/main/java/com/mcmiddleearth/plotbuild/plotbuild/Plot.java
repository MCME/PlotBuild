/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl
 */
public class Plot {
    
    @Getter
    private Location corner1;
    
    @Getter
    private Location corner2;
    
    @Getter
    private final List <Player> owners = new ArrayList <>();
    
    @Getter
    @Setter
    private boolean finished = false;
    
    @Getter
    @Setter
    private boolean accepted = false;
    
    @Getter
    @Setter
    private boolean refused = false;
    
    public Plot(Location corner1, Location corner2) throws InvalidPlotLocationException {
        if(corner1 == null || corner2 == null || corner1.getWorld() != corner2.getWorld()) {
            throw new InvalidPlotLocationException();
        }
        this.corner1 = new Location(corner1.getWorld(), corner1.getBlockX(), 0, corner1.getBlockZ());
        this.corner2 = new Location(corner2.getWorld(), corner2.getBlockX(), 0, corner2.getBlockZ());
    }
    
}
