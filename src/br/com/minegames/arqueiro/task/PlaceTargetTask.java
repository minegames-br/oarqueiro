package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.arqueiro.domain.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.Target;
import br.com.minegames.arqueiro.domain.WallBlockTarget;
import br.com.minegames.util.BlockManipulationUtil;
import br.com.minegames.util.Utils;

public class PlaceTargetTask extends BukkitRunnable {
	
	private Game game;
	
	public PlaceTargetTask(Game game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	int index = new Random().nextInt(3);
    	
    	if(game.getTargets().size() == 0 ) {
    		Bukkit.getConsoleSender().sendMessage(Utils.color("&6Creating Target"));
    		createGroundTarget();
    	} else if(game.getTargets().size() == 1) {
    		Target target = game.getTargets().get(0);
    		if(target instanceof WallBlockTarget) {
    			createGroundTarget();
    		} else if (target instanceof GroundBlockTarget ){
    			createFloatingTarget();
    		} else {
    			createWallTarget();
    		}
    	}
        
    }
    
    private void createGroundTarget() {
    	
    	Location l = game.getRandomSpawnLocationForGroundTarget();
    	
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		
    	Block block = createNewBlock(game.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y-2, z, DyeColor.WHITE );
    	
    	game.addTarget(new GroundBlockTarget(this.game, block));
    }
    
    private void createWallTarget() {
    	Location l = game.getRandomSpawnLocationForWallTarget();
    	
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		
    	Block block = createNewBlock(game.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	
    	game.addTarget(new WallBlockTarget(this.game, block));
    }

    /**
     * Criar um alvo que fica fora da parede e longe do chão
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type
     * @return
     */
    private void createFloatingTarget() {
    	Location l = game.getRandomSpawnLocationForFloatingTarget();
    	
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		
    	Block block = createNewBlock(game.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(game.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	
    	game.addTarget(new FloatingBlockTarget(this.game, block));
    }
    
    private Block createNewBlock(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	return block;
    }

}
