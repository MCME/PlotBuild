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

import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.data.Selection;
import com.mcmiddleearth.plotbuild.exceptions.InvalidPlotLocationException;
import com.mcmiddleearth.pluginutil.plotStoring.IStoragePlot;
import com.mcmiddleearth.pluginutil.plotStoring.InvalidRestoreDataException;
import com.mcmiddleearth.pluginutil.plotStoring.MCMEPlotFormat;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class Plot implements IStoragePlot {
    
    private static final int allowedDist = 5; //area around own plot in which a player gets creative mode
    @Getter
    private Location corner1;
    
    @Getter
    private Location corner2;
    
    /**
     * Allways use BukkitTools.isSame(OflinePlayer, OfflinePlayer) or
     * OfflinePlayer.getUniqueID() to check if a player is in 
     * this List
     */
    private List <UUID> owners = new ArrayList <>();
    
    @Getter
    private PlotState state;
    
    private PlotBorder border;
    
    @Getter
    @Setter
    private PlotBuild plotbuild;
    
    @Getter
    @Setter
    private boolean usingRestoreData = true;
    
    private static final String ext = ".mcme";

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
        border = new PlotBorder(this);
        border.placeBorder();
        border.placeSigns();
    }
    
    public Plot(Location corner1, Location corner2, List <UUID> owners, PlotState state, LinkedList <Location> border) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.owners = owners;
        this.state = state;
        this.border = new PlotBorder(this);
        this.border.setBorder(border);
    }
    
    public boolean isInside(Location location) {
        return isInside(location, 0);
    }
    
    public boolean isInsideWithBorder(Location location) {
        return isInside(location, 1);
    }
    
    private boolean isInside(Location location, int tolerance) {
        if(    location.getBlockX() < corner1.getBlockX() - tolerance 
           || location.getBlockX() > corner2.getBlockX() + tolerance
           ||  location.getBlockZ() < corner1.getBlockZ() - tolerance
           || location.getBlockZ() > corner2.getBlockZ() + tolerance){
            return false;
        }
        if( plotbuild.isCuboid()
            && (location.getBlockY() < corner1.getBlockY() - tolerance
                || location.getBlockY() > corner2.getBlockY() +  tolerance) ) {
            return false;
        }
        return true;
    }
    
    public boolean isNear(Location location) {
        if(    location.getBlockX() < corner1.getBlockX() - allowedDist 
            || location.getBlockX() > corner2.getBlockX() + allowedDist
            || location.getBlockZ() < corner1.getBlockZ() - allowedDist 
            || location.getBlockZ() > corner2.getBlockZ() + allowedDist){
            return false;
        }
        if( plotbuild.isCuboid()
            && (   location.getBlockY() < corner1.getBlockY() - allowedDist
                || location.getBlockY() > corner2.getBlockY() + allowedDist)) {
            return false;
        }
        return true;
    }
    
    public boolean isIntersecting(Selection selection, boolean cuboid) {
        int spacing = 1;
        int selMinX = Math.min(selection.getFirstPoint().getBlockX(),selection.getSecondPoint().getBlockX());
        int selMaxX = Math.max(selection.getFirstPoint().getBlockX(),selection.getSecondPoint().getBlockX());
        int selMinZ = Math.min(selection.getFirstPoint().getBlockZ(),selection.getSecondPoint().getBlockZ());
        int selMaxZ = Math.max(selection.getFirstPoint().getBlockZ(),selection.getSecondPoint().getBlockZ());
        if(selMinX > corner2.getBlockX() + spacing 
                || selMaxX < corner1.getBlockX() - spacing) {
            return false;
        }
        if(selMinZ > corner2.getBlockZ() + spacing 
                || selMaxZ < corner1.getBlockZ() - spacing) {
            return false;
        }
        if(plotbuild.isCuboid() && cuboid) {
            int selMinY = Math.min(selection.getFirstPoint().getBlockY(),selection.getSecondPoint().getBlockY());
            int selMaxY = Math.max(selection.getFirstPoint().getBlockY(),selection.getSecondPoint().getBlockY());
            if(selMinY > corner2.getBlockY() + spacing 
                    || selMaxY < corner1.getBlockY() - spacing) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isOwner(OfflinePlayer player) {
        for(UUID offPlayer : owners) {
            //Player search = offPlayer.getPlayer();
            if(player!=null && offPlayer.equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    public int countOwners() {
        return owners.size();
    }
    
    public List<UUID> getOfflineOwners() {
        return owners;
    }
    
    public boolean claim(OfflinePlayer player){
        owners.add(player.getUniqueId());
        state = PlotState.CLAIMED;
        //border.refreshBorder();
        return border.placeSigns();
    }
    
    public boolean invite(OfflinePlayer player){
        if(!owners.contains(player.getUniqueId())) {
            owners.add(player.getUniqueId());
        }
        return border.placeSigns();
    }
    
    public boolean remove(OfflinePlayer player){
        if(owners.size()>1) {
            owners.remove(player.getUniqueId());
        }
        return border.placeSigns();
    }
    
    public boolean unclaim() throws InvalidRestoreDataException{
        owners.removeAll(owners);
        reset();
        state = PlotState.UNCLAIMED;
        //border.refreshBorder();
        return border.placeSigns();
    }
    
    public boolean leave(OfflinePlayer player) {
        owners.remove(player.getUniqueId());
        return border.placeSigns();
    }
    
    public boolean finish(){
        state = PlotState.FINISHED;
        //border.refreshBorder();
        return border.placeSigns();
    }
    
    public boolean refuse(){
        state = PlotState.REFUSED;
        //border.refreshBorder();
        return border.placeSigns();
    }
    
    public void accept() throws InvalidRestoreDataException{
        delete(true);
    }
    
    public boolean clear(boolean unclaim) throws InvalidRestoreDataException {
        reset();
        if(unclaim) {
            unclaim();
        }
        return border.placeSigns();
    }
    
    public void delete(boolean keep) throws InvalidRestoreDataException{
        if(!keep && state != PlotState.UNCLAIMED) {
            reset();
        }
        state = PlotState.REMOVED;
        border.removeSigns();
        border.removeBorder();
        PluginData.refreshPlotNearbyBorders(this);
    }
    
    public boolean placeSigns() {
        return border.placeSigns();
    }
    
    public List<Location> getBorder() {
        return border.getBorder();
    }
    
    public void save() throws IOException {
        if (this.isUsingRestoreData()) {
            try(DataOutputStream out = new DataOutputStream(
                                       new BufferedOutputStream(
                                       new GZIPOutputStream(
                                       new FileOutputStream(PluginData.getFile(this, ext)))))) {
                new MCMEPlotFormat().save(this, out);
                out.flush();
                out.close();
            }
        }
    }
    
    private void reset() throws InvalidRestoreDataException {
        try(DataInputStream in = new DataInputStream(
                                 new BufferedInputStream(
                                 new GZIPInputStream(
                                 new FileInputStream(PluginData.getFile(this, ext)))))) {
            new MCMEPlotFormat().load(this, in);
            /*List <MaterialData> restoreData = PluginData.getRestoreData(plotbuild, this);
            if (restoreData == null) {
            return;
            }
            List<Entity> entities = new ArrayList<>();
            entities.addAll(getCorner1().getWorld().getEntitiesByClass(Painting.class));
            entities.addAll(getCorner1().getWorld().getEntitiesByClass(ItemFrame.class));
            entities.addAll(getCorner1().getWorld().getEntitiesByClass(ArmorStand.class));
            for(Entity entity: entities) {
            if(isInside(entity.getLocation())) {
            entity.remove();
            }
            }
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
            listindex++;
            }
            }
            }
            PluginData.restoreComplexBlocks(plotbuild, this);
            final Plot thisPlot = this;
            new BukkitRunnable() {
            @Override
            public void run() {
            PluginData.restoreEntities(plotbuild,thisPlot);
            }
            }.runTaskLater(PlotBuildPlugin.getPluginInstance(),4);*/
        } catch (IOException ex) {
            throw new InvalidRestoreDataException();
        }
    }
    
    public int getID() {
        return plotbuild.getPlots().indexOf(this)+1;
    }
    
    @Override
    public World getWorld() {
        return corner1.getWorld();
    }
    
    @Override
    public Location getLowCorner() {
        int miny = 0;
        if(getPlotbuild().isCuboid()) {
            miny = getCorner1().getBlockY();
        }
        return new Location(corner1.getWorld(), corner1.getBlockX(),
                                                miny,
                                                corner1.getBlockZ());
    }
    
    @Override
    public Location getHighCorner() {
        int maxy = corner1.getWorld().getMaxHeight()-1;
        if(getPlotbuild().isCuboid()) {
            maxy = getCorner2().getBlockY();
        }
        return new Location(corner2.getWorld(), corner2.getBlockX(),
                                                maxy,
                                                corner2.getBlockZ());
    }
}
