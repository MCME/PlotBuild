/* 
 *  Copyright (C) 2015 Minecraft Middle Earth
 * 
 *  This file is part of PlotBuild.
 * 
 *  PlotBuild is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PlotBuild is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PlotBuild.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class Plot {
    
    @Getter
    private Location corner1;
    
    @Getter
    private Location corner2;
    
    private List <OfflinePlayer> owners = new ArrayList <>();
    
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
        plotbuild.getPlots().add(this);
        placeBorder();
        placeSigns();
    }
    
    public Plot(Location corner1, Location corner2, List <OfflinePlayer> owners, PlotState state, List <Location> border) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.owners = owners;
        this.state = state;
        this.border = border;
    }
    
    public boolean isInside(Location location) {
        if(    location.getBlockX() < corner1.getBlockX() || location.getBlockX() > corner2.getBlockX()
           ||  location.getBlockZ() < corner1.getBlockZ() || location.getBlockZ() > corner2.getBlockZ()){
            return false;
        }
        if( plotbuild.isCuboid()
            && (location.getBlockY() < corner1.getBlockY() || location.getBlockY() > corner2.getBlockY())) {
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
            if(selMinY > corner2.getBlockY() || selMaxY < corner1.getBlockY()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isOwner(OfflinePlayer player) {
        for(OfflinePlayer offPlayer : owners) {
            //Player search = offPlayer.getPlayer();
            if(player!=null && offPlayer.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    public int countOwners() {
        return owners.size();
    }
    
    /*public boolean isOfflineOwner(OfflinePlayer player) {
        return owners.contains(player);
    }*/
    
    public List<OfflinePlayer> getOfflineOwners() {
        return owners;
    }
    
    public void claim(OfflinePlayer player){
        owners.add(player);
        state = PlotState.CLAIMED;
        refreshBorder();
        placeSigns();
    }
    
    public void invite(OfflinePlayer player){
        if(!owners.contains(player)) {
            owners.add(player);
            placeSigns();
        }
    }
    
    public void remove(OfflinePlayer player){
        if(owners.size()>1) {
            owners.remove(player);
            placeSigns();
        }
    }
    
    public void unclaim() throws InvalidRestoreDataException{
        owners.removeAll(owners);
        state = PlotState.UNCLAIMED;
        reset();
        refreshBorder();
        placeSigns();
    }
    
    public void leave(OfflinePlayer player) {
        owners.remove(player);
        placeSigns();
    }
    
    public void finish(){
        state = PlotState.FINISHED;
        refreshBorder();
    }
    
    public void refuse(){
        state = PlotState.REFUSED;
        refreshBorder();
    }
    
    public void accept() throws InvalidRestoreDataException{
        delete(true);
    }
    
    public void clear(boolean unclaim) throws InvalidRestoreDataException {
        if(unclaim) {
            unclaim();
        }
        reset();
    }
    
    public void delete(boolean keep) throws InvalidRestoreDataException{
        if(!keep) {
            reset();
        }
        state = PlotState.REMOVED;
        removeSigns();
        removeBorder();
    }
    
    public final void placeSigns(){
        if(border.size()>0 && state!=PlotState.REMOVED) {
            refreshBorder();
            Block signBlock = border.get(0).getBlock().getRelative(0, 3, -1);
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                signBlock.setType(Material.WALL_SIGN);
                Sign sign = (Sign) signBlock.getState();
                sign.setLine(0,plotbuild.getName()); 
                sign.setLine(1,"#"+getID());
                sign.setLine(3,"Builder:");
                sign.update();
            }
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                if(owners.size()>0) {
                    signBlock.setType(Material.WALL_SIGN);
                    Sign sign = (Sign) signBlock.getState();
                    for(int i = 0; i<4; i++) {
                        if(i < owners.size()) {
                            sign.setLine(i, owners.get(i).getName());
                        }
                        else {
                            sign.setLine(i, "");
                        }
                    }
                    sign.update();
                }
                else {
                    signBlock.setType(Material.AIR);
                }
            }
            signBlock = signBlock.getRelative(0,-1,0);
            if(signBlock.isEmpty() || signBlock.getType()==Material.WALL_SIGN) {
                if(owners.size()>4) {
                    signBlock.setType(Material.WALL_SIGN);
                    Sign sign = (Sign) signBlock.getState();
                    for(int i = 4; i<8; i++) {
                        if(i < owners.size()) {
                            sign.setLine(i-4, owners.get(i).getName());
                        }
                        else {
                            sign.setLine(i-4, "");
                        }
                    }
                    sign.update();
                }
                else {
                    signBlock.setType(Material.AIR);
                }
            }
        }
    }
    
    private void removeSigns(){
        if(border.size()>0) {
            Block signBlock = border.get(0).getBlock().getRelative(0, 3, -1);
            signBlock.setType(Material.AIR);
            signBlock = signBlock.getRelative(0,-1,0);
            signBlock.setType(Material.AIR);
            signBlock = signBlock.getRelative(0,-1,0);
            signBlock.setType(Material.AIR);
        }
    }
    
    private void placeBorder(){
        if(plotbuild.getBorderType()!=BorderType.NONE) {
            for(int i = corner1.getBlockX()-1; i<=corner2.getBlockX()+1;i++){
                this.placeWoolBlock(i, plotbuild.getBorderHeight(), corner1.getBlockZ()-1);
                this.placeWoolBlock(i, plotbuild.getBorderHeight(), corner2.getBlockZ()+1);
            }
            for(int i = corner1.getBlockZ(); i<=corner2.getBlockZ();i++){
                this.placeWoolBlock(corner1.getBlockX()-1, plotbuild.getBorderHeight(), i);
                this.placeWoolBlock(corner2.getBlockX()+1, plotbuild.getBorderHeight(), i);
            }  
        }
        else {
            this.placeWoolBlock(corner1.getBlockX()-1, plotbuild.getBorderHeight(), corner1.getBlockZ()-1);
        }
        if(!border.isEmpty()){
            Location first = border.get(0);
            placeWoolBlock(first.getBlockX(),first.getBlockY()+1,first.getBlockZ());
            placeWoolBlock(first.getBlockX(),first.getBlockY()+2,first.getBlockZ());
            placeWoolBlock(first.getBlockX(),first.getBlockY()+3,first.getBlockZ());
        }
    }
 
    @SuppressWarnings("deprecation")
    private void placeWoolBlock(int x, int y, int z){
    	Block currentBlock=corner1.getWorld().getBlockAt(x,y,z);
    	if(plotbuild.getBorderType()==BorderType.GROUND){
            if(currentBlock.isEmpty()) {
                do {
                    currentBlock = corner1.getWorld().getBlockAt(x,y,z);
                    y--;
                } while(currentBlock.isEmpty() 
                            && !(plotbuild.isCuboid() && (y+1<corner1.getBlockY() || y+1>corner2.getBlockY())));
                currentBlock = corner1.getWorld().getBlockAt(x,y+2,z); 
            }
            else {
                do {
                    currentBlock = corner1.getWorld().getBlockAt(x,y,z);
                    y++;
                } while(!currentBlock.isEmpty()
                            && !(plotbuild.isCuboid() && (y-1<corner1.getBlockY() || y-1>corner2.getBlockY())));
                currentBlock = corner1.getWorld().getBlockAt(x,y-1,z); 
           }
        }
    	if(currentBlock.isEmpty()) {
            currentBlock.setType(Material.WOOL);
            currentBlock.setData((byte) state.getState());            
            border.add(currentBlock.getLocation());
        }
    }

    private void refreshBorder(){
        for(Location loc : border) {
            Block block = corner1.getWorld().getBlockAt(loc);
            block.setType(Material.WOOL);
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
    
    private void reset() throws InvalidRestoreDataException {
        List <MaterialData> restoreData = PluginData.getRestoreData(plotbuild, this);
        int miny = 0;
        int maxy = getCorner1().getWorld().getMaxHeight()-1;
        if(getPlotbuild().isCuboid()) {
            miny = getCorner1().getBlockY();
            maxy = getCorner2().getBlockY();
        }
        Selection sel = new Selection();
        sel.setFirstPoint(corner1);
        sel.setSecondPoint(corner2);
        if(restoreData.size() != sel.getArea() * (maxy - miny + 1)) {
            throw new InvalidRestoreDataException();
        }
        int listindex = 0;
        for(int x = getCorner1().getBlockX(); x <= getCorner2().getBlockX(); ++x) {
            for(int y = miny; y <= maxy; ++y) {
                for(int z = getCorner1().getBlockZ(); z <= getCorner2().getBlockZ(); ++z) {
                    Location loc = new Location(getCorner1().getWorld(), x, y, z);
                    loc.getBlock().setType(restoreData.get(listindex).getItemType(), false);
                    loc.getBlock().setData(restoreData.get(listindex).getData(), false);
                    /*BlockState bstate = loc.getBlock().getState();
                    bstate.setType(restoreData.get(listindex).getItemType());
                    bstate.setRawData(restoreData.get(listindex).getData());
                    bstate.update(false);*/
                    listindex++;
                }
            }
        }
    }
    
    public int getID() {
        return plotbuild.getPlots().indexOf(this)+1;
    }
}
