/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.plotbuild.utils;

import java.lang.reflect.Field;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

/**
 *
 * @author Eriol_Eandur
 */
public class HeadUtil {
    
    public static void placeCustomHead(Block block, ItemStack head) {
        try {
            BlockState blockState = block.getState();
            blockState.setType(Material.SKULL);
            blockState.update(true, false);
            blockState = block.getState();
            Skull skullData = (Skull) blockState;
            skullData.setSkullType(SkullType.PLAYER);
            Field profileField = head.getItemMeta().getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            Object profile = profileField.get(head.getItemMeta());
            profileField = skullData.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullData, profile);
            skullData.setRawData((byte)1);
            skullData.setRotation(BlockFace.NORTH_NORTH_EAST);
            skullData.update(true, false);
        } catch (NoSuchFieldException | SecurityException e) {
            Bukkit.getLogger().log(Level.SEVERE, "No such method exception during reflection.", e);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to use reflection.", e);
        }
    }

    public static ItemStack pickCustomHead(Skull skullBlockState) {
        try {
            Field profileField = skullBlockState.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            Object profile = profileField.get(skullBlockState);

            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            ItemMeta headMeta = head.getItemMeta();
            
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
            head.setItemMeta(headMeta);
            return head;
        } catch (NoSuchFieldException | SecurityException e) {
            Bukkit.getLogger().log(Level.SEVERE, "No such method exception during reflection.", e);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to use reflection.", e);
        }
        return null;
    }

}
