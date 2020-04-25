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

import org.bukkit.Location;

/**
 *
 * @author Eriol_Eandur
 */
public class Selection {
    
    Location firstPoint;
    Location secondPoint;
    

    public boolean isValid(){
        return  firstPoint != null 
            && secondPoint != null
            && firstPoint.getWorld() == secondPoint.getWorld();
    }
    
    public int getArea() {
        return (Math.abs(firstPoint.getBlockX() - secondPoint.getBlockX()) + 1) *
               (Math.abs(firstPoint.getBlockZ() - secondPoint.getBlockZ()) + 1);
    }
    
    public int getVolume() {
        return (Math.abs(firstPoint.getBlockY() - secondPoint.getBlockY()) + 1) * getArea();
    }

    public Location getFirstPoint() {
        return firstPoint;
    }

    public void setFirstPoint(Location firstPoint) {
        this.firstPoint = firstPoint;
    }

    public Location getSecondPoint() {
        return secondPoint;
    }

    public void setSecondPoint(Location secondPoint) {
        this.secondPoint = secondPoint;
    }
}