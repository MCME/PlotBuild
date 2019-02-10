/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.boydti.fawe.object.clipboard.ReadOnlyClipboard;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.exceptions.InvalidRestoreDataException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 *
 * @author Eriol_Eandur
 */
public class SchematicPlotFormat implements PlotFormat {

    private static final String ext = ".schem";
    
    @Override
    public void save(Plot plot) throws IOException {
        Location corner1 = plot.getCorner1().clone();
        Location corner2 = plot.getCorner2().clone();
        if(!plot.getPlotbuild().isCuboid()) {
            corner1.setY(0);
            corner2.setY(corner1.getWorld().getMaxHeight());
        }
        //CuboidSelection selection = new CuboidSelection(corner1.getWorld(),corner1,corner2);
        BukkitWorld world = new BukkitWorld(corner1.getWorld());//session.getWorld().getName());
        CuboidRegion region = new CuboidRegion(world, new Vector(corner1.getBlockX(),corner1.getBlockY(),corner1.getBlockZ()),
                                                      new Vector(corner2.getBlockX(),corner2.getBlockY(),corner2.getBlockZ()));
        EditSession session = new EditSessionBuilder(region.getWorld()).allowedRegionsEverywhere().autoQueue(false).build();
        Clipboard clipboard = new BlockArrayClipboard(region, ReadOnlyClipboard.of(session, region));
        Schematic schematic = new Schematic(clipboard);
        schematic.save(PluginData.getFile(plot,ext), ClipboardFormat.STRUCTURE);
        
        schematic.save(PluginData.getFile(plot,ext), ClipboardFormat.STRUCTURE);
    }

    @Override
    public void load(Plot plot) throws InvalidRestoreDataException {
        if(plot.getState().equals(PlotState.UNCLAIMED)) {
            return;
        }
        File schemFile = PluginData.getFile(plot,ext);
        if(!schemFile.exists()) {
            return;
        }
        try {
Logger.getGlobal().info("reset plot");
            Schematic schematic = ClipboardFormat.STRUCTURE.load(schemFile);
            //Schematic schematic = ClipboardFormats.findByAlias("SPONGE_SCHEMATIC").load(schemFile);
Logger.getGlobal().info("Schematic: "+schematic);
            if(schematic!=null) {
                EditSession session = WorldEdit.getInstance().getEditSessionFactory()
                                               .getEditSession(new BukkitWorld(plot.getCorner1().getWorld()), -1);
                Location corner1 = plot.getCorner1().clone();
                if(!plot.getPlotbuild().isCuboid()) {
                    corner1.setX(0);
                }
                schematic.paste(session, new Vector(corner1.getBlockX(),
                                                    corner1.getBlockY(),
                                                    corner1.getBlockZ()), true);
                session.flushQueue();
            } else {
                throw new InvalidRestoreDataException();
            }
        } catch (IOException ex) {
            throw new InvalidRestoreDataException();
        }
    }

    
}
