/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.plotbuild;

import com.mcmiddleearth.plotbuild.PlotBuildPlugin;
import com.mcmiddleearth.plotbuild.data.PluginData;
import com.mcmiddleearth.plotbuild.utils.NMSUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
/*import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_13_R2.NBTReadLimiter;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.TileEntity;*/
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
/*import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;*/
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

/**
 *
 * @author Eriol_Eandur
 */
public class MCMEPlotFormat implements PlotFormat {
    
    private static final String ext = ".mcme";
    //private static final String entityExt = ".emcme";
    
    /*
     * Binary file:
     * 4: number of palette entries
     *     Palette entry:
     *     4:          <datalength> length of palette entry data
     *     <datalength>: blockData in UTF-8 charset
     * All blocks in area, ordered by x,y,z:
     *     Block Entry:
     *     4: Index of palette entries for this block
     * All Tile Entities in area
     *     4: number of Tile Entities
     *     variable: nbt data of Tile Entities
     * Other Entities in area (Paintings, Item Frames, Armor Stands)
     *     4: number of Entities
     *     variable: nbt data of Entities
    */
    
    @Override
    public void save(Plot plot) throws IOException {
        if (plot.isUsingRestoreData()) {
            World world = plot.getCorner1().getWorld();
            int miny = 0;
            int maxy = world.getMaxHeight()-1;
            if(plot.getPlotbuild().isCuboid()) {
                miny = plot.getCorner1().getBlockY();
                maxy = plot.getCorner2().getBlockY();
            }
            try(DataOutputStream out = new DataOutputStream(
                                       new BufferedOutputStream(
                                       new GZIPOutputStream(
                                       new FileOutputStream(PluginData.getFile(plot, ext)))))) {
                /*String name = world.getName();
                byte[] nameBytes = name.getBytes(Charset.forName("UTF-8"));
                out.write(nameBytes.length);
                out.write(nameBytes);*/
                //List<Object> complexBlocks = new ArrayList<>();
                Map<BlockData,Integer> paletteMap = new HashMap<>();
                List<BlockData> palette = new ArrayList<>();
                for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
                    for(int y = miny; y <= maxy; ++y) {
                        for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                            Block block = world.getBlockAt(x, y, z);
                            if(paletteMap.get(block.getBlockData())==null) {
                                paletteMap.put(block.getBlockData(), palette.size());
                                palette.add(block.getBlockData());
                            }
                        }
                    }
                }
                out.writeInt(palette.size()); //write length of palette
                for(int i=0; i<palette.size();i++) {
                    String blockDataString = palette.get(i).getAsString();
                    byte[] blockDataBytes = blockDataString.getBytes(Charset.forName("UTF-8"));
                    out.writeInt(blockDataBytes.length); //write length of next blockdata
                    out.write(blockDataBytes);
                }
                for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
                    for(int y = miny; y <= maxy; ++y) {
                        for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                            Block block = world.getBlockAt(x, y, z);
                            //out.write(block.getType().getId());
                            //out.write(block.getData());
                            out.writeInt(paletteMap.get(block.getBlockData()));
                        }
                    }
                }
                List tileEntities = new ArrayList();
                for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
                    for(int y = miny; y <= maxy; ++y) {
                        for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                            Block block = world.getBlockAt(x, y, z);
                            BlockState state = block.getState();
                            if(NMSUtil.getCraftBukkitClass("block.CraftBlockEntityState").isInstance(state)) {
                                Object nbt = NMSUtil.invokeCraftBukkit("block.CraftBlockEntityState","getSnapshotNBT",
                                                                       null, state);
//Logger.getGlobal().info("save nbt TileEntity: "+nbt.toString());
                                tileEntities.add(nbt);
                            }
                        }
                    }
                }
                out.writeInt(tileEntities.size());
                for(Object nbt: tileEntities) {
                    Class[] argsClasses = new Class[]{NMSUtil.getNMSClass("NBTTagCompound"),DataOutput.class};
                    NMSUtil.invokeNMS("NBTCompressedStreamTools","a",argsClasses,null,nbt,(DataOutput)out);
                }
                Collection<Entity> entities = plot.getCorner1().getWorld()
                     .getNearbyEntities(new BoundingBox(plot.getCorner1().getBlockX(),
                                                        miny,
                                                        plot.getCorner1().getBlockZ(),
                                                        plot.getCorner2().getBlockX(),
                                                        maxy,
                                                        plot.getCorner2().getBlockZ()),
                            new MCMEEntityFilter());
                out.writeInt(entities.size());
                for(Entity entity: entities) {
                    Object nbt = NMSUtil.createNMSObject("NBTTagCompound");
                    Object nmsEntity = NMSUtil.invokeCraftBukkit("entity.CraftEntity", "getHandle",
                                                                 null, entity);
                    NMSUtil.invokeNMS("NBTTagCompound","setString",null, nbt,"id", 
                                      NMSUtil.invokeNMS("Entity","getSaveID",null, nmsEntity));
                    nbt = NMSUtil.invokeNMS("Entity","save",null, nmsEntity,nbt);
                    Class[] argsClasses = new Class[]{NMSUtil.getNMSClass("NBTTagCompound"),DataOutput.class};
                    NMSUtil.invokeNMS("NBTCompressedStreamTools","a",argsClasses,null,nbt,(DataOutput)out);
                }
                out.flush();
                out.close();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MCMEPlotFormat.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*Collection<Entity> entities = plot.getCorner1().getWorld()
                     .getNearbyEntities(new BoundingBox(plot.getCorner1().getBlockX(),
                                                        miny,
                                                        plot.getCorner1().getBlockZ(),
                                                        plot.getCorner2().getBlockX(),
                                                        maxy,
                                                        plot.getCorner2().getBlockZ()),
                            new MCMEEntityFilter());
            EntityUtil.store(PluginData.getFile(plot, entityExt), entities);*/
        }
    }
    

    @Override
    public void load(Plot plot) throws IOException {
        try(DataInputStream in = new DataInputStream(
                                 new BufferedInputStream(
                                 new GZIPInputStream(
                                 new FileInputStream(PluginData.getFile(plot, ext)))))) {
            int miny = 0;
            int maxy = plot.getCorner1().getWorld().getMaxHeight()-1;
            if(plot.getPlotbuild().isCuboid()) {
                miny = plot.getCorner1().getBlockY();
                maxy = plot.getCorner2().getBlockY();
            }
            Collection<Entity> entities = plot.getCorner1().getWorld()
                     .getNearbyEntities(new BoundingBox(plot.getCorner1().getBlockX(),
                                                        miny,
                                                        plot.getCorner1().getBlockZ(),
                                                        plot.getCorner2().getBlockX(),
                                                        maxy,
                                                        plot.getCorner2().getBlockZ()),
                            new MCMEEntityFilter());
            for(Entity entity: entities) {
                entity.remove();
            }
            
            int paletteLength = in.readInt();
            Map<Integer,BlockData> palette = new HashMap<>(paletteLength);
            for(int i = 0; i<paletteLength; i++) {
                int dataLength = in.readInt();
                byte[] byteData = new byte[dataLength];
                in.readFully(byteData);
                BlockData blockData = Bukkit.getServer().createBlockData(new String(byteData,Charset.forName("UTF-8")));
                palette.put(i, blockData);
            }
            for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
                for(int y = miny; y <= maxy; ++y) {
                    for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                        Location loc = new Location(plot.getCorner1().getWorld(), x, y, z);
                        loc.getBlock().setBlockData(palette.get(in.readInt()),false);
                    }
                }
            }
            int tileEntityLength = in.readInt();
            for(int i=0; i< tileEntityLength; i++) {
                Class[] argsClasses = new Class[]{DataInput.class,NMSUtil.getNMSClass("NBTReadLimiter")};
                Object nbt = NMSUtil.invokeNMS("NBTCompressedStreamTools","a",argsClasses,null,
                                               (DataInput)in,NMSUtil.getNMSField("NBTReadLimiter","a",null));  
                Object nmsWorld = NMSUtil.invokeCraftBukkit("CraftWorld", "getHandle", null, plot.getCorner1().getWorld());
                Object entity = NMSUtil.invokeNMS("TileEntity","create",null, null,nbt,nmsWorld);
                NMSUtil.invokeNMS("WorldServer","setTileEntity",null,nmsWorld,
                                  NMSUtil.invokeNMS("TileEntity","getPosition",null,entity), entity);
//Logger.getGlobal().info("load nbt TileEntity: "+nbt.toString());
            }
            final int entityLength = in.readInt();
            List entityDatas = new ArrayList();
            for(int i=0; i<entityLength; i++) {
                Class[] argsClasses = new Class[]{DataInput.class,NMSUtil.getNMSClass("NBTReadLimiter")};
                entityDatas.add(NMSUtil.invokeNMS("NBTCompressedStreamTools","a",argsClasses,
                                                  null,(DataInput)in,
                                NMSUtil.getNMSField("NBTReadLimiter","a",null)));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for(Object nbt: entityDatas) {
                        Object nmsWorld = NMSUtil.invokeCraftBukkit("CraftWorld", "getHandle", null,
                                                                    plot.getCorner1().getWorld());
                        try {
                            Class[] argsClasses = new Class[]{NMSUtil.getNMSClass("NBTTagCompound"),
                                                              NMSUtil.getNMSClass("World")};
                            Object entity = NMSUtil.invokeNMS("EntityTypes","a",argsClasses,null,nbt,nmsWorld);
                            argsClasses = new Class[]{NMSUtil.getNMSClass("Entity"),
                                                      CreatureSpawnEvent.SpawnReason.CUSTOM.getClass()};
                            NMSUtil.invokeNMS("WorldServer","addEntity",argsClasses,nmsWorld,entity, 
                                              CreatureSpawnEvent.SpawnReason.CUSTOM);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(MCMEPlotFormat.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }.runTaskLater(PlotBuildPlugin.getPluginInstance(), 20);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MCMEPlotFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*File entityFile = PluginData.getFile(plot, entityExt);
        try {
            EntityUtil.restore(entityFile, new ArrayList<>());
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    private class MCMEEntityFilter implements Predicate<Entity>{

        @Override
        public boolean test(Entity entity) {
            return entity instanceof Painting
                || entity instanceof ItemFrame
                || entity instanceof ArmorStand;
        }
    
    }
    
    /*private static void savePlotRestoreEntityData(Plot plot, File file) {
        List<Entity> entities = new ArrayList<>();
        entities.addAll(plot.getCorner1().getWorld().getEntitiesByClass(Painting.class));
        entities.addAll(plot.getCorner1().getWorld().getEntitiesByClass(ItemFrame.class));
        entities.addAll(plot.getCorner1().getWorld().getEntitiesByClass(ArmorStand.class));
        Location cor1 = plot.getCorner1();
        Location cor2 = plot.getCorner2();
        List<Entity> plotEntities = new ArrayList<>();
        if (plot.isUsingRestoreData()) {
            for(Entity entity : entities) {
                Location loc = entity.getLocation();
                if(plot.isInside(loc)) {
                    plotEntities.add(entity);
                }
            }
        }
        try {
            EntityUtil.store(file, plotEntities);
        } catch (IOException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void savePlotRestoreBlockData(Plot plot, File file) throws IOException {
        FileWriter fw = new FileWriter(file.toString());
        PrintWriter writer = new PrintWriter(fw);
        if (plot.isUsingRestoreData()) {
            World world = plot.getCorner1().getWorld();
            writer.println(world.getName());
            int miny = 0;
            int maxy = world.getMaxHeight()-1;
            if(plot.getPlotbuild().isCuboid()) {
                miny = plot.getCorner1().getBlockY();
                maxy = plot.getCorner2().getBlockY();
            }
            List<Object> complexBlocks = new ArrayList<>();
            for(int x = plot.getCorner1().getBlockX(); x <= plot.getCorner2().getBlockX(); ++x) {
                for(int y = miny; y <= maxy; ++y) {
                    for(int z = plot.getCorner1().getBlockZ(); z <= plot.getCorner2().getBlockZ(); ++z) {
                        Block block = world.getBlockAt(x, y, z);
                        writer.println(block.getType());
                        writer.println(block.getData());
                        if(!BlockUtil.isSimple(block)) {
                            complexBlocks.add(block);
                        }
                    }
                }
            }
            BlockUtil.store(new File(file.toString()+"c"), complexBlocks);
        } else {
            writer.println("<!NODATA!>");
        }
        writer.close();
    }
    
    }*/
    
}
