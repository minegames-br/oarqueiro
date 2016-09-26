package br.com.minegames.util;

import java.io.Serializable;
import org.bukkit.Location;
 
public class Region
  implements Serializable
{
  private static final long serialVersionUID = -757210694361145167L;
  private String name;
  private int minX;
  private int minY;
  private int minZ;
  private int maxX;
  private int maxY;
  private int maxZ;
  private String world;
 
  public Region(String name, Location pos1, Location pos2)
  {
    this.name = name;
    this.world = pos1.getWorld().getName();
 
    if (pos1.getX() > pos2.getX())
    {
      this.minX = pos2.getBlockX();
      this.maxX = pos1.getBlockX();
    }
    else
    {
      this.minX = pos1.getBlockX();
      this.maxX = pos2.getBlockX();
    }
 
    if (pos1.getBlockY() > pos2.getBlockY())
    {
      this.minY = pos2.getBlockY();
      this.maxY = pos1.getBlockY();
    }
    else
    {
      this.minY = pos1.getBlockY();
      this.maxY = pos2.getBlockY();
    }
 
    if (pos1.getBlockZ() > pos2.getBlockZ())
    {
      this.minZ = pos2.getBlockZ();
      this.maxZ = pos1.getBlockZ();
    }
    else
    {
      this.minZ = pos1.getBlockZ();
      this.maxZ = pos2.getBlockZ();
    }
  }
 
  public String getName() {
    return this.name;
  }
 
  public Boolean isIn(Location location) {
    Double x = Double.valueOf(location.getBlockX());
    Double y = Double.valueOf(location.getBlockY());
    Double z = Double.valueOf(location.getBlockZ());
 
    if (location.getWorld().getName().equalsIgnoreCase(this.world))
    {
      if ((this.minX <= x.doubleValue()) && (x.doubleValue() <= this.maxX))
      {
        if ((this.minY <= y.doubleValue()) && (y.doubleValue() <= this.maxY))
        {
          if ((this.minZ <= z.doubleValue()) && (z.doubleValue() <= this.maxZ))
          {
            return true;
          }
        }
      }
    }
    return false;
  }
}