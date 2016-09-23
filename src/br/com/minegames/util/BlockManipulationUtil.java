package br.com.minegames.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

public class BlockManipulationUtil {
	
    public static Block createNewWool(World world, double x, double y, double z, DyeColor color) {
    	
    	Location targetLocation = new Location(world, x, y, z);
    	//Bukkit.getConsoleSender().sendMessage(Utils.color("&6Creating New Block " + targetLocation + " - " + world));
        Block block = world.getBlockAt(targetLocation);
    	//Bukkit.getConsoleSender().sendMessage(Utils.color("&6world.getBlockAt" + world.getBlockAt(targetLocation)));
    	
       	block.setType(Material.WOOL);
       	if(color != null) {
       		BlockState state = block.getState();//Grab its generic BlockState
       		Wool woolData = (Wool)state.getData();//Since we just set the block type to wool, we should be fine casting its data to Wool
       		woolData.setColor(color);//Set the new color
       		state.setData(woolData);//Re-apply the new Wool data to the BlockState
       		state.update();//Update the BlockState to finish the changes
       	}
       	return block;
    }
    
    public static void createWoolBlocks(Location l1, Location l2, DyeColor color) {
    	List<Block> blocks = blocksFromTwoPoints(l1, l2);
    	for(Block block: blocks) {
       		createNewWool(block.getWorld(), block.getX(), block.getY(), block.getZ(), color);
    	}
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<Block>();
 
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                   
                    blocks.add(block);
                }
            }
        }
       
        return blocks;
    }

	public static void clearBlocks(Location l1, Location l2) {
    	List<Block> blocks = blocksFromTwoPoints(l1, l2);
    	for(Block block: blocks) {
    		block.setType(Material.AIR);
    	}
	}
}
