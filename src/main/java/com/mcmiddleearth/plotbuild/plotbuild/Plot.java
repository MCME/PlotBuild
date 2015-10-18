/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.command.PlotNew;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class Plot {
    
    @Getter
    private Location corner1;
    
    @Getter
    private Location corner2;
    
    @Getter
    private List <OfflinePlayer> owners = new ArrayList <>();
    
    /*
    @Getter
    @Setter
    private boolean finished = false;
    
    @Getter
    @Setter
    private boolean accepted = false;
    
    @Getter
    @Setter
    private boolean refused = false;
    */
    
    @Getter
    private PlotState state;
    
    @Getter
    private List <Location> border = new ArrayList <>();
    
    @Getter
    @Setter
    private PlotBuild plotbuild;
    
    public Plot(PlotBuild plotbuild, Location corner1, Location corner2) throws InvalidPlotLocationException {
        if(corner1 == null || corner2 == null || corner1.getWorld() != corner2.getWorld()) {
            throw new InvalidPlotLocationException();
        }
        this.plotbuild = plotbuild;
        this.corner1 = new Location(corner1.getWorld(), Math.min(corner1.getBlockX(), corner2.getBlockX()),
                                                        Math.min(corner1.getBlockY(), corner2.getBlockY()),
                                                        Math.min(corner1.getBlockZ(), corner2.getBlockZ()));
        this.corner2 = new Location(corner1.getWorld(), Math.max(corner1.getBlockX(), corner2.getBlockX()),
                                                        Math.max(corner1.getBlockY(), corner2.getBlockY()), 
                                                        Math.max(corner1.getBlockZ(), corner2.getBlockZ()));
        this.state = PlotState.UNCLAIMED;
        placeBorder();
    }
    
    public Plot(Location corner1, Location corner2, List <OfflinePlayer> owners, PlotState state, List <Location> border) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.owners = owners;
        this.state = state;
        this.border = border;
    }
    
    public boolean isInside(Location location) {
        if(    location.getBlockX() <= corner1.getBlockX() || location.getBlockX() >= corner2.getBlockX()
           ||  location.getBlockZ() <= corner1.getBlockZ() || location.getBlockZ() >= corner2.getBlockZ()){
            return false;
        }
        if( plotbuild.isCuboid()
            && (location.getBlockY() <= corner1.getBlockY() || location.getBlockY() >= corner2.getBlockY())) {
            return false;
        }
        return true;
    }
    
    public boolean isIntersecting(Selection selection, boolean cuboid) {
        int selMinX = Math.min(selection.getFirstPoint().getBlockX(),selection.getSecondPoint().getBlockX());
        int selMaxX = Math.max(selection.getFirstPoint().getBlockX(),selection.getSecondPoint().getBlockX());
        int selMinZ = Math.min(selection.getFirstPoint().getBlockZ(),selection.getSecondPoint().getBlockZ());
        int selMaxZ = Math.max(selection.getFirstPoint().getBlockZ(),selection.getSecondPoint().getBlockZ());
        if(selMinX > corner2.getBlockX() || selMaxX < corner1.getBlockX()) {
            return false;
        }
        if(selMinZ > corner2.getBlockZ() || selMaxZ < corner1.getBlockZ()) {
            return false;
        }
        if(plotbuild.isCuboid() && cuboid) {
            int selMinY = Math.min(selection.getFirstPoint().getBlockY(),selection.getSecondPoint().getBlockY());
            int selMaxY = Math.max(selection.getFirstPoint().getBlockY(),selection.getSecondPoint().getBlockY());
            Logger.getLogger(PlotNew.class.getName()).info(selMinY+" "+selMaxY+" "+corner1.getBlockY()+" "+corner2.getBlockY());
            if(selMinY > corner2.getBlockY() || selMaxY < corner1.getBlockY()) {
                return false;
            }
        }
        return true;
    }
    
    public void claim(OfflinePlayer player){
        owners.add(player);
        state = PlotState.CLAIMED;
        coloriseBorder();
    }
    
    public void invite(OfflinePlayer player){
        if(!owners.contains(player)) {
            owners.add(player);
        }
    }
    
    public void remove(OfflinePlayer player){
        if(owners.size()>1) {
            owners.remove(player);
        }
    }
    
    public void unclaim(){
        owners.removeAll(owners);
        state = PlotState.UNCLAIMED;
        coloriseBorder();
    }
    
    public void leave(OfflinePlayer player) {
        owners.remove(player);
    }
    
    public void finish(){
        state = PlotState.FINISHED;
        coloriseBorder();
    }
    
    public void refuse(){
        state = PlotState.REFUSED;
        coloriseBorder();
    }
    
    public void accept(){
        delete(true);
    }
    
    public void clear(boolean unclaim) {
        if(unclaim) {
            unclaim();
        }
        //TODO reset plot
    }
    
    public void delete(boolean keep){
        if(!keep) {
            //TODO reset plot
        }
        state = PlotState.REMOVED;
        removeBorder();
    }
    
    private void placeBorder(){
        for(int i = corner1.getBlockX()-1; i<=corner2.getBlockX()+1;i++){
            this.placeWoolBlock(i, plotbuild.getBorderHeight(), corner1.getBlockZ()-1);
            this.placeWoolBlock(i, plotbuild.getBorderHeight(), corner2.getBlockZ()+1);
        }
        for(int i = corner1.getBlockZ(); i<=corner2.getBlockZ();i++){
            this.placeWoolBlock(corner1.getBlockX()-1, plotbuild.getBorderHeight(), i);
            this.placeWoolBlock(corner2.getBlockX()+1, plotbuild.getBorderHeight(), i);
        }  
    }
 
    @SuppressWarnings("deprecation")
    private void placeWoolBlock(int x, int y, int z){
    	Block currentBlock;
    	switch(plotbuild.getBorderType()){
            case GROUND:
	   	do {
		    currentBlock = corner1.getWorld().getBlockAt(x,y,z);
		    y--;
		} while(currentBlock.isEmpty());
		currentBlock = corner1.getWorld().getBlockAt(x,y+2,z); break;
            case FLOAT:
    		currentBlock = corner1.getWorld().getBlockAt(x,y,z); break;
            default: return;
    	}
    	if(currentBlock.isEmpty()) {
            currentBlock.setType(Material.WOOL);
            currentBlock.setData((byte) state.getState());            
            border.add(currentBlock.getLocation());
        }
    }

    private void coloriseBorder(){
        for(Location loc : border) {
            Block block = corner1.getWorld().getBlockAt(loc);
            block.setData((byte) state.getState());
        }
    }
    
    private void removeBorder() {
        for(Location loc : border) {
            Block block = corner1.getWorld().getBlockAt(loc);
            block.setType(Material.AIR);
            block.setData((byte) 0);
        }
        border.removeAll(border);
    }
    
}
