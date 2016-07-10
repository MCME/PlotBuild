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
package com.mcmiddleearth.plotbuild.data;

import com.mcmiddleearth.plotbuild.PlotBuildPlugin;
import com.mcmiddleearth.plotbuild.constants.BorderType;
import com.mcmiddleearth.plotbuild.constants.Permission;
import com.mcmiddleearth.plotbuild.constants.PlotState;
import com.mcmiddleearth.plotbuild.conversations.NewPlotConversationFactory;
import com.mcmiddleearth.plotbuild.conversations.PlotBuildConversationFactory;
import com.mcmiddleearth.plotbuild.plotbuild.Plot;
import com.mcmiddleearth.plotbuild.plotbuild.PlotBuild;
import com.mcmiddleearth.plotbuild.utils.BlockUtil;
import com.mcmiddleearth.plotbuild.utils.EntityUtil;
import com.mcmiddleearth.plotbuild.utils.FileUtil;
import com.mcmiddleearth.plotbuild.utils.ListUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Ivan1pl, Eriol_Eandur
 */
public class PluginData {
    
    @Setter
    @Getter
    private static PlotBuildConversationFactory confFactory;
    
    @Setter
    @Getter
    private static NewPlotConversationFactory newPlotFactory;
    
    @Getter
    private static int claimCooldownTics = 400;
    
    @Getter
    private static final List<UUID> claimCooldownList = new ArrayList<>();
    
    @Getter
    private static final List <UUID> switchedToCreative = new ArrayList<>(); 
    
    @Getter
    private static final List <PlotBuild> plotbuildsList = new ArrayList <>();
    
    private static final Map <UUID, PlotBuild> currentPlotbuild = new LinkedHashMap <>();
    
    private static final Map <UUID, Selection> selections = new LinkedHashMap <>();
    
    private static final Map <UUID, List<String>> offlineMessages = new LinkedHashMap<>();
    
    @Getter
    private static final Set <String> missingWorlds = new HashSet<>();
    
    @Getter
    private static boolean loaded = false;
    
    @Getter
    private static List <String> protectedWorlds = new ArrayList<>();
    
    private static final File plotBuildDir = new File(PlotBuildPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "plotbuilds");
    
    private static final File messageFile = new File(PlotBuildPlugin.getPluginInstance().getDataFolder()
                                                   + File.separator + "offlineMessages.msg");
    static {
        if(!plotBuildDir.exists()) {
            plotBuildDir.mkdirs();
        }
        if(!messageFile.exists()) {
            try {
                messageFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setCurrentPlotbuild(Player p, PlotBuild plotbuild) {
        currentPlotbuild.put(p.getUniqueId(), plotbuild);
    }
    
    public static PlotBuild getCurrentPlotbuild(Player p) {
        return currentPlotbuild.get(p.getUniqueId());
    }
    
    public static Selection getCurrentSelection(Player p){
        Selection selection = selections.get(p.getUniqueId());
        if(selection == null) {
            selection = new Selection();
            selections.put(p.getUniqueId(), selection);
        }
        return selection;
    }
    
    public static void clearSelection(Player player) {
        UUID found = null;
        for(UUID search : selections.keySet()) {
            if(search.equals(player.getUniqueId())) {
                found = search;
                break;
            }
        }
        if(found!=null) {
            selections.remove(found);
        }
    }
    
    public static List<Plot> getOwnedPlots(OfflinePlayer player) {
        List<Plot> plots = new ArrayList<>();
        for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
            for(Plot plot : plotbuild.getPlots()) {
                if(plot.getState()!=PlotState.REMOVED && plot.isOwner(player)) {
                    plots.add(plot);
                }
            }
        }
        return plots;
    }
    
    public static Set<UUID> getBuilders() {
        Set<UUID> builders = new HashSet<>();
        for(PlotBuild plotbuild : PluginData.getPlotbuildsList()) {
            builders.addAll(plotbuild.getBuilders());
        }
        return builders;
    }
    
    public static Plot getPlotAt(Location location) {
        for(PlotBuild plotbuild : plotbuildsList) {
            for(Plot plot : plotbuild.getPlots()) {
                if(plot.isInside(location) && plot.getState()!=PlotState.REMOVED) {
                    return plot;
                }
            }
        }
        return null;
    }
    
    public static boolean isNearOwnPlot(Player player) {
        for(PlotBuild plotbuild : plotbuildsList) {
            for(Plot plot : plotbuild.getPlots()) {
                if(plot.getState()!=PlotState.REMOVED
                        && plot.isOwner(player) 
                        && plot.isNear(player.getLocation())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Plot getIntersectingPlot(Selection selection, boolean cuboid) {
        for(PlotBuild plotbuild : plotbuildsList) {
            for(Plot plot : plotbuild.getPlots()) {
                if(plot.getState()!=PlotState.REMOVED && plot.isIntersecting(selection,cuboid)) {
                    return plot;
                }
            }
        }
        return null;
    }
    
    public static PlotBuild getPlotBuild(String name) {
        for(PlotBuild plotbuild : plotbuildsList) {
            if(plotbuild.getName().equalsIgnoreCase(name)) {
                return plotbuild;
            }
        }
        return null;
    }
    
    public static void refreshPlotNearbyBorders(Plot centerPlot) {
        Selection selection = new Selection();
        selection.setFirstPoint(centerPlot.getCorner1().getBlock().getRelative(-2, -2, -2).getLocation());
        selection.setSecondPoint(centerPlot.getCorner2().getBlock().getRelative(2, 2, 2).getLocation());
        for(PlotBuild plotbuild:plotbuildsList) {
            for(Plot plot:plotbuild.getPlots()) {
                if(plot.isIntersecting(selection, centerPlot.getPlotbuild().isCuboid())) {
                    plot.placeSigns();
                }
            }
        }
    }
    
    public static void addOfflineMessage(OfflinePlayer player, String message) {
        if(offlineMessages.containsKey(player.getUniqueId())) {
            List<String> messages = offlineMessages.get(player.getUniqueId());
            messages.add(message);
        }
        else {
            List<String> messages = new ArrayList<>();
            messages.add(message);
            offlineMessages.put(player.getUniqueId(), messages);
        }
    }
    
    public static List<String> getOfflineMessagesFor(OfflinePlayer player) {
        for(UUID offline: offlineMessages.keySet()) {
            if(offline!=null){
                if(offline.equals(player.getUniqueId())) {
                    return offlineMessages.get(offline);
                }
            }
        }
        return null;
    }
    
    public static void deleteOfflineMessagesFor(OfflinePlayer player) {
        UUID found = null;
        for(UUID offline: offlineMessages.keySet()) {
            if(offline!=null){
                if(offline.equals(player.getUniqueId())) {
                    found = offline;
                    break;
                }
            }
        }
        if(found != null) {
            offlineMessages.remove(found);
        }
    }
    
    public static void saveData() {
        try {
            saveOfflineMessages();
        } catch (IOException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(PlotBuild plotbuild : plotbuildsList) {
            try {
                savePlotBuild(plotbuild);
            } catch (IOException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void loadData() {
        PlotBuildPlugin.getPluginInstance().getLogger().info("Loading plotbuilds...");
        protectedWorlds = PlotBuildPlugin.getPluginInstance().getConfig().getStringList("protectedWorlds");
        claimCooldownTics = PlotBuildPlugin.getPluginInstance().getConfig().getInt("claimCooldown",20)*20;
        FilenameFilter pbFilter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String string) {
                return string.endsWith(".pb");
            }
            
        };
        for(File f : plotBuildDir.listFiles(pbFilter)) {
            try {
                loadPlotBuild(f);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!missingWorlds.isEmpty()) {
            plotbuildsList.clear();
            PlotBuildPlugin.getPluginInstance().getLogger().info("Waiting for worlds to load:");
            for(String world : missingWorlds) {
                PlotBuildPlugin.getPluginInstance().getLogger().info(world);
            }
        } else {
            loaded = true;
            PlotBuildPlugin.getPluginInstance().getLogger().info("All plotbuilds loaded.");
        }
        try {
            loadOfflineMessages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List <MaterialData> getRestoreData(PlotBuild plotbuild, Plot plot) {
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        File plotRestoreData = new File(plotDir, Integer.toString(plotbuild.getPlots().indexOf(plot)) + ".r");
        ArrayList <MaterialData> ret = new ArrayList<>();
        try {
            try (Scanner scanner = new Scanner(plotRestoreData)) {
                String firstLine = scanner.nextLine();
                if (!("<!NODATA!>".equals(firstLine))) {
                    while(scanner.hasNext()) {
                        Material material = Material.valueOf(scanner.nextLine());
                        byte data = scanner.nextByte();
                        scanner.nextLine();
                        ret.add(new MaterialData(material, data));
                    }
                } else {
                    ret = null;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public static void restoreComplexBlocks(PlotBuild plotbuild, Plot plot) {
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        File plotRestoreData = new File(plotDir, Integer.toString(plotbuild.getPlots().indexOf(plot)) + ".rc");
        try {
            BlockUtil.restore(plotRestoreData, new ArrayList<Entity>(), new ArrayList<BlockState>(), true);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void restoreEntities(PlotBuild plotbuild, Plot plot) {
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        File plotRestoreData = new File(plotDir, Integer.toString(plotbuild.getPlots().indexOf(plot)) + ".e");
        try {
            EntityUtil.restore(plotRestoreData, new ArrayList<Entity>());
        } catch (IOException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean deletePlotBuild(PlotBuild plotbuild) {
        File plotBuildFile = new File(plotBuildDir, plotbuild.getName()+".pb");
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        plotbuildsList.remove(plotbuild);
        currentPlotbuild.values().removeAll(Collections.singleton(plotbuild));
        try {
            boolean pbf = plotBuildFile.delete();
            boolean dr = FileUtil.deleteRecursive(plotDir);
            return pbf && dr;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean hasPermissionsToBuild(Player player, Location location) {
        Plot plot = getPlotAt(location);
        return (plot != null 
                && (plot.isOwner(player) || plot.getPlotbuild().isStaff(player)) 
                && player.hasPermission(Permission.USER));
    }
    
    public static boolean hasNoPermissionsToBuild(Player player, Location location) {
        Plot plot = getPlotAt(location);
        return (!player.hasPermission(Permission.SUPERVISOR) && plot != null && !plot.isOwner(player) && !plot.getPlotbuild().isStaff(player));
    }
    
    public static boolean canSelectArea(Player player) {
        PlotBuild pb = getCurrentPlotbuild(player);
        return player.hasPermission(Permission.STAFF) || (pb == null ? false : pb.isStaff(player));
    }
    
    private static void savePlotBuild(PlotBuild plotbuild) throws IOException {
        File plotBuildFile = new File(plotBuildDir, plotbuild.getName()+".pb");
        File plotDir = new File(plotBuildDir, plotbuild.getName());
        plotBuildFile.createNewFile();
        plotDir.mkdir();
        if(plotBuildFile.exists() && plotDir.exists()) {
            FileWriter fw = new FileWriter(plotBuildFile.toString());
            PrintWriter writer = new PrintWriter(fw);
            writer.println(ListUtil.playerListToString(plotbuild.getOfflineStaffList()));
            writer.println(ListUtil.playerListToString(plotbuild.getOfflineBannedPlayers()));
            writer.println(plotbuild.isLocked());
            writer.println(plotbuild.isPriv());
            writer.println(plotbuild.isCuboid());
            writer.println(plotbuild.getBorderType());
            writer.println(plotbuild.getBorderHeight());
            for(String entry : plotbuild.getHistory()) {
                writer.println(entry);
            }
            writer.close();
            for(int i = 0; i < plotbuild.getPlots().size(); ++i) {
                savePlot(plotbuild.getPlots().get(i), plotDir, i);
            }
        } else {
            throw new IOException();
        }
    }
    
    private static void savePlot(Plot plot, File plotDir, int i) throws IOException {
        File plotDataFile = new File(plotDir, Integer.toString(i) + ".p");
        //File plotRestoreFile = new File(plotDir, Integer.toString(i) + ".r");
        //File plotEntityRestoreFile = new File(plotDir, Integer.toString(i) + ".e");
        //boolean saveRestoreData = !plotRestoreFile.exists();
        plotDataFile.createNewFile();
        //plotRestoreFile.createNewFile();
        if(plotDataFile.exists() ){//&& plotRestoreFile.exists()) {
            FileWriter fw = new FileWriter(plotDataFile.toString());
            PrintWriter writer = new PrintWriter(fw);
            writer.println(plot.isUsingRestoreData());
            writer.println(plot.getCorner1().getWorld().getName());
            writer.println(plot.getCorner1().getBlockX() + " "
                         + plot.getCorner1().getBlockY() + " "
                         + plot.getCorner1().getBlockZ());
            writer.println(plot.getCorner2().getWorld().getName());
            writer.println(plot.getCorner2().getBlockX() + " "
                         + plot.getCorner2().getBlockY() + " "
                         + plot.getCorner2().getBlockZ());
            writer.println(ListUtil.playerListToString(plot.getOfflineOwners()));
            writer.println(plot.getState());
            for(Location l : plot.getBorder()) {
                writer.println(l.getWorld().getName());
                writer.println(l.getBlockX() + " "
                             + l.getBlockY() + " "
                             + l.getBlockZ());
            }
            writer.close();
            /*if(saveRestoreData) {
                savePlotRestoreBlockData(plot, plotRestoreFile);
                savePlotRestoreEntityData(plot, plotEntityRestoreFile);
            }*/
        } else {
            throw new IOException();
        }
    }
    
    public static void savePlotRestoreData(Plot plot) throws IOException {
        File plotDir = new File(plotBuildDir,plot.getPlotbuild().getName());
        int plotIndex = plot.getPlotbuild().getPlots().indexOf(plot);
        savePlotRestoreBlockData(plot, new File(plotDir, Integer.toString(plotIndex) + ".r"));
        savePlotRestoreEntityData(plot, new File(plotDir, Integer.toString(plotIndex) + ".e"));
    }
    
    private static void savePlotRestoreEntityData(Plot plot, File file) {
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
    
    private static void loadPlotBuild(File f) throws FileNotFoundException {
        String name = f.getName();
        name = name.substring(0, name.length()-3);
        Scanner scanner = new Scanner(f);
        List <UUID> staffList = ListUtil.playerListFromString(scanner.nextLine());
        List <UUID> bannedList = ListUtil.playerListFromString(scanner.nextLine());
        boolean locked = scanner.nextBoolean();
        scanner.nextLine();
        boolean priv = scanner.nextBoolean();
        scanner.nextLine();
        boolean cuboid = scanner.nextBoolean();
        scanner.nextLine();
        BorderType borderType = BorderType.fromString(scanner.nextLine());
        int borderHeight = scanner.nextInt();
        scanner.nextLine();
        ArrayList <String> history = new ArrayList<>();
        while(scanner.hasNext()) {
            history.add(scanner.nextLine());
        }
        scanner.close();
        File plotDir = new File(plotBuildDir, name);
        plotDir.mkdirs();
        List <Plot> plots = loadPlots(plotDir);
        PlotBuild plotbuild = new PlotBuild(name, borderType, borderHeight, priv, cuboid);
        for(Plot p : plots) {
            p.setPlotbuild(plotbuild);
        }
        plotbuild.setLocked(locked);
        plotbuild.setStaffList(staffList);
        plotbuild.setBannedPlayers(bannedList);
        plotbuild.setPlots(plots);
        plotbuild.setHistory(history);
        plotbuildsList.add(plotbuild);
    }
    
    private static List <Plot> loadPlots(File plotDir) throws FileNotFoundException {
        FilenameFilter pFilter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String string) {
                return string.endsWith(".p");
            }
            
        };
        File[] files = plotDir.listFiles(pFilter);
        ArrayList <Plot> plots = new ArrayList<>(Collections.nCopies(files.length, (Plot) null));
        for(File f : files) {
            String name = f.getName();
            name = name.substring(0, name.length()-2);
            int i = Integer.parseInt(name);
            plots.set(i, loadPlot(f));
        }
        return plots;
    }
    
    private static Plot loadPlot(File f) throws FileNotFoundException {
        Scanner scanner = new Scanner(f);
        boolean useRestoreData = "true".equalsIgnoreCase(scanner.nextLine());
        String worldName1 = scanner.nextLine();
        World world1 = Bukkit.getWorld(worldName1);
        if(world1 == null) {
            missingWorlds.add(worldName1);
        }
        List <Integer> coords1 = ListUtil.integersFromString(scanner.nextLine(), ' ');
        String worldName2 = scanner.nextLine();
        World world2 = Bukkit.getWorld(worldName2);
        if(world2 == null) {
            missingWorlds.add(worldName2);
        }
        List <Integer> coords2 = ListUtil.integersFromString(scanner.nextLine(), ' ');
        Location corner1 = new Location(world1, coords1.get(0), coords1.get(1), coords1.get(2));
        Location corner2 = new Location(world2, coords2.get(0), coords2.get(1), coords2.get(2));
        List <UUID> ownersList = ListUtil.playerListFromString(scanner.nextLine());
        PlotState state = PlotState.valueOf(scanner.nextLine());
        LinkedList <Location> border = new LinkedList<>();
        while(scanner.hasNext()) {
            String worldName = scanner.nextLine();
            World world = Bukkit.getWorld(worldName);
            if(world == null) {
                missingWorlds.add(worldName);
            }
            List <Integer> coords = ListUtil.integersFromString(scanner.nextLine(), ' ');
            Location location = new Location(world, coords.get(0), coords.get(1), coords.get(2));
            border.add(location);
        }
        scanner.close();
        Plot ret = new Plot(corner1, corner2, ownersList, state, border);
        ret.setUsingRestoreData(useRestoreData);
        return ret;
    }

    private static void saveOfflineMessages() throws IOException{
        messageFile.createNewFile();
        if(messageFile.exists()) {
            FileWriter fw = new FileWriter(messageFile.toString());
            PrintWriter writer = new PrintWriter(fw);
            for(UUID player : offlineMessages.keySet()) {
                writer.println(player.toString());
                writer.println(ListUtil.messageListToString(offlineMessages.get(player)));
            }
            writer.close();
        } else {
            throw new IOException();
        }
    }

    private static void loadOfflineMessages() throws FileNotFoundException {
        Scanner scanner = new Scanner(messageFile);
        while(scanner.hasNext()) {
            UUID player = UUID.fromString(scanner.nextLine());
            offlineMessages.put(player, ListUtil.messagesFromString(scanner.nextLine()));
        }
        scanner.close();
    }
    
}
