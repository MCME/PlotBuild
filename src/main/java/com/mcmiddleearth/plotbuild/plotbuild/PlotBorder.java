/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.utils.BukkitUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

/**
 *
 * @author Eriol_Eandur
 */
public class PlotBorder {
    
    private final Plot plot;

    private LinkedList<Location> border;
    
    private List<Location> entityLocs = new ArrayList<>();
    
    PlotBorder(Plot plot) {
        this.plot = plot;
        border = new LinkedList<>();
    }
    
    private boolean checkSignBlock(Block block0) {
            Block signBlock = block0.getRelative(0, 3, -1);
            if(plot.getPlotbuild().isCuboid() && signBlock.getLocation().getBlockY()>plot.getCorner2().getBlockY()) {
                return false;
            }
            if(signBlock.getType()!=Material.AIR)
                return false;
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.getType()!=Material.AIR)
                return false;
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.getType()!=Material.AIR)
                return false;
            signBlock = signBlock.getRelative(0,0,1);
            if(signBlock.getType()!=Material.AIR && signBlock.getType()!=Material.WOOL)
                return false;
            signBlock = signBlock.getRelative(0,1,0);
            if(signBlock.getType()!=Material.AIR && signBlock.getType()!=Material.WOOL)
                return false;
            signBlock = signBlock.getRelative(0,1,0);
            if(signBlock.getType()!=Material.AIR && signBlock.getType()!=Material.WOOL)
                return false;
            return true;
    }
    
    private boolean findSignBlock() {
        /*int nbOfTries;
        if(plotbuild().isCuboid()) {
            nbOfTries = 4*(corner2().getBlockY()-corner1().getBlockY()+2*(corner2().getBlockZ()-corner1().getBlockZ()));
        }
        else {
            nbOfTries = 2*(corner2().getBlockZ()-corner1().getBlockZ());
        }*/
        for(int i = 0; i<border.size();i++){// && i<nbOfTries;i++) {
            Location loc = border.get(i);
            Block block = corner1().getWorld().getBlockAt(loc);
            if(checkSignBlock(block)) {
                border.remove(loc);
                border.addFirst(loc);
                placeWoolBlock(loc.getBlock().getRelative(0,1,0));
                placeWoolBlock(loc.getBlock().getRelative(0,2,0));
                placeWoolBlock(loc.getBlock().getRelative(0,3,0));
                return true;
            }
        }
        return false;
    }
    
    boolean placeSigns(){
        refreshEntityLocations();
        if(plot.getState()!=PlotState.REMOVED) {
            if(border.size()==0) {
                return false;
            }
            removeSigns();
            removeBorder();
            placeBorder();
            if(!findSignBlock()){
                return false;
            }
            Block signBlock = border.get(0).getBlock().getRelative(0, 3, -1);
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                signBlock.setType(Material.WALL_SIGN);
                if(signBlock.getState() instanceof Sign) {
                    Sign sign = (Sign) signBlock.getState();
                    sign.setLine(0,plotbuild().getName()); 
                    sign.setLine(1,"#"+plot.getID());
                    sign.setLine(3,"Builder:");
                    sign.update();
                }
            }
            signBlock = signBlock.getRelative(0,-1,0);
            List<UUID> owners = plot.getOfflineOwners();
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                if(owners.size()>0) {
                    signBlock.setType(Material.WALL_SIGN);
                    if(signBlock.getState() instanceof Sign) {
                        Sign sign = (Sign) signBlock.getState();
                        for(int i = 0; i<4; i++) {
                            if(i < owners.size()) {
                                sign.setLine(i, Bukkit.getOfflinePlayer(owners.get(i)).getName());
                            }
                            else {
                                sign.setLine(i, "");
                            }
                        }
                        sign.update();
                    }
                }
                else {
                    signBlock.setType(Material.AIR);
                }
            }
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                if(owners.size()>4) {
                    signBlock.setType(Material.WALL_SIGN);
                    if(signBlock.getState() instanceof Sign) {
                        Sign sign = (Sign) signBlock.getState();
                        for(int i = 4; i<8; i++) {
                            if(i < owners.size()) {
                                sign.setLine(i-4, Bukkit.getOfflinePlayer(owners.get(i)).getName());
                            }
                            else {
                                sign.setLine(i-4, "");
                            }
                        }
                        sign.update();
                    }
                }
                else {
                    signBlock.setType(Material.AIR);
                }
            }
        }
        return true;
    }
    
    void removeSigns(){
        if(border.size()>0) {
            Block signBlock = border.get(0).getBlock().getRelative(0, 3, -1);
            if(signBlock.getType()==Material.WALL_SIGN) {
                signBlock.setType(Material.AIR);
            }
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.getType()==Material.WALL_SIGN){
                signBlock.setType(Material.AIR);
            }
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.getType()==Material.WALL_SIGN){
                signBlock.setType(Material.AIR);
            }
         //}
            Location signLoc = border.get(0);
            int signBlockX = signLoc.getBlockX();
            int signBlockZ = signLoc.getBlockZ();
            List<Location> removeList = new ArrayList<>();
            for(Location loc : border) {
                if(loc != signLoc 
                        && loc.getBlockX()==signBlockX && loc.getBlockZ()==signBlockZ 
                        && (plotbuild().getBorderType()!=BorderType.BOX || loc.getBlockY()<corner1().getBlockY()-1 
                                                                        || loc.getBlockY()>corner2().getBlockY()+1)) {
                    if(loc.getBlock().getType() == Material.WOOL) {
                        loc.getBlock().setType(Material.AIR);
                    }
                    removeList.add(loc);
                }
            }
            border.removeAll(removeList);
        }
    }
    
    void placeBorder(){
        refreshEntityLocations();
        if(plotbuild().getBorderType()!=BorderType.NONE) {
            for(int i = corner2().getBlockZ()+1; i>=corner1().getBlockZ()-1;i--){
                this.placeBorderColumn(corner1().getBlockX()-1, plotbuild().getBorderHeight(), i);
                this.placeBorderColumn(corner2().getBlockX()+1, plotbuild().getBorderHeight(), i);
            }  
            for(int i = corner1().getBlockX(); i<=corner2().getBlockX();i++){
                this.placeBorderColumn(i, plotbuild().getBorderHeight(), corner1().getBlockZ()-1);
                this.placeBorderColumn(i, plotbuild().getBorderHeight(), corner2().getBlockZ()+1);
            }
        }
        else {
            this.placeBorderColumn(corner1().getBlockX()-1, plotbuild().getBorderHeight(), corner1().getBlockZ()-1);
        }
        if(!border.isEmpty()){
            Location first = border.get(0);
            placeWoolBlock(corner1().getWorld().getBlockAt(first.getBlockX(),first.getBlockY()+1,first.getBlockZ()));
            placeWoolBlock(corner1().getWorld().getBlockAt(first.getBlockX(),first.getBlockY()+2,first.getBlockZ()));
            placeWoolBlock(corner1().getWorld().getBlockAt(first.getBlockX(),first.getBlockY()+3,first.getBlockZ()));
        }
    }
 
    private void placeBorderColumn(int x, int y, int z){
    	Block currentBlock=corner1().getWorld().getBlockAt(x,y,z);
    	if(plotbuild().getBorderType()==BorderType.GROUND
                || plotbuild().getBorderType()==BorderType.NONE){
            if(currentBlock.isEmpty()) {
                do {
                    currentBlock = corner1().getWorld().getBlockAt(x,y,z);
                    y--;
                } while(currentBlock.isEmpty() 
                            && !(plotbuild().isCuboid() && (y+1<corner1().getBlockY() || y+1>corner2().getBlockY())));
                currentBlock = corner1().getWorld().getBlockAt(x,y+2,z); 
            }
            else {
                do {
                    currentBlock = corner1().getWorld().getBlockAt(x,y,z);
                    y++;
                } while(!currentBlock.isEmpty()
                            && !(plotbuild().isCuboid() && (y-1<corner1().getBlockY() || y-1>corner2().getBlockY())));
                currentBlock = corner1().getWorld().getBlockAt(x,y-1,z); 
           }
        }
    	if(plotbuild().isCuboid()) {
            if(isCorner(x,z)) {
                for(int i = corner1().getBlockY(); i<=corner2().getBlockY(); i++) {
                    placeWoolBlock(corner1().getWorld().getBlockAt(x,i,z));
                }
            }
            currentBlock = corner1().getWorld().getBlockAt(x,corner1().getBlockY()-1,z);
            placeWoolBlock(currentBlock);
            currentBlock = corner2().getWorld().getBlockAt(x,corner2().getBlockY()+1,z);
        }
        placeWoolBlock(currentBlock);
    }
        
    private boolean isCorner(int x, int z){
        return (   corner1().getBlockX()-1==x && corner1().getBlockZ()-1==z
                || corner1().getBlockX()-1==x && corner2().getBlockZ()+1==z
                || corner2().getBlockX()+1==x && corner1().getBlockZ()-1==z
                || corner2().getBlockX()+1==x && corner2().getBlockZ()+1==z);
    }
        
    private void placeWoolBlock(Block block) {
        if(block.isEmpty() && ! isEntityBlock(block.getLocation())) {
            block.setType(Material.WOOL);
            block.setData((byte) plot.getState().getState());            
            border.add(block.getLocation());
        }
    }

    /*void refreshBorder(){
        for(Location loc : border) {
            Block block = corner1().getWorld().getBlockAt(loc);
            if(block.isEmpty() || block.getType() == Material.WOOL) {
                block.setType(Material.WOOL);
                block.setData((byte) plot.getState().getState());
            }
        }
    }*/
    
    void removeBorder() {
        if(border.size()>0) {
            for(Location loc : border) {
                Block block = corner1().getWorld().getBlockAt(loc);
                if(block.getType().equals(Material.WOOL)) {
                    block.setType(Material.AIR);
                    block.setData((byte) 0);
                }
            }
            border.removeAll(border);
        }
    }
    
    void setBorder(LinkedList<Location> border) {
        this.border = border;
    }
    
    List<Location> getBorder() {
        return border;
    }
    
    private PlotBuild plotbuild() {
        return plot.getPlotbuild();
    }
    
    private Location corner1() {
        return plot.getCorner1();
    }
    
    private Location corner2() {
        return plot.getCorner2();
    }
   
    private void refreshEntityLocations() {
        entityLocs.clear();
        Collection<Entity> entityList = new ArrayList<>();
        entityList.addAll(corner1().getWorld().getEntitiesByClass(Painting.class));
        entityList.addAll(corner1().getWorld().getEntitiesByClass(ItemFrame.class));
        for(Entity entity : entityList) {
            int x = entity.getLocation().getBlockX();
            int z = entity.getLocation().getBlockZ();
            if(   (    x>corner1().getBlockX()-2 
                    && x<corner2().getBlockX()+2 
                    && ( z==corner1().getBlockZ()-1 || z==corner2().getBlockZ()+1))
               || (    z>corner1().getBlockZ()-2 
                    && z<corner2().getBlockZ()+2 
                    && ( x==corner1().getBlockX()-1 || x==corner2().getBlockX()+1))) {
                entityLocs.add(entity.getLocation());
            }
        }
    }
    
    private boolean isEntityBlock(Location loc) {
        for(Location entityLoc : entityLocs) {
            if(BukkitUtil.isSameBlock(entityLoc, loc)) {
                return true;
            }
        }
        return false;
    }
}
