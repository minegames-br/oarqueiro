package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.core.util.BlockManipulationUtil;
import com.thecraftcloud.domain.MyCloudCraftGame;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.TheLastArcher;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;

public class PlaceTargetTask extends BukkitRunnable {
	
	private GameController controller;
	private BlockManipulationUtil blockManipulationUtil = new BlockManipulationUtil();
	
	public PlaceTargetTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {
    	MyCloudCraftGame game = controller.getMyCloudCraftGame();
    	if(!game.isStarted()) {
    		return;
    	}

    	int configValue = (int) controller.getGameArenaConfig(Constants.MAX_TARGET);
    	if(controller.getTargets().size() >= configValue ) {
    		return;
    	}
    	
    	int index = (controller.getTargets().size() % 2);

    	if( index == 0 ) {
			createGroundTarget();
    	} else {
			createFloatingTarget();
    	}
    	
    }
    
    /**
     * Criar um alvo que fica no chão
     * @return
     */
    private void createGroundTarget() {
    	Location l = controller.getRandomSpawnLocationForGroundTarget();
    	Block block = createTarget(l);
    	blockManipulationUtil.createNewWool(controller.getWorld(), l.getBlockX(), l.getBlockY()-2, l.getBlockZ(), DyeColor.WHITE );
    	controller.addTarget(new GroundBlockTarget(block));
    }

    /**
     * Criar um alvo que fica fora da parede e longe do chão
     * @return
     */
    private void createFloatingTarget() {
    	Location l = controller.getRandomSpawnLocationForFloatingTarget();
    	Block block = createTarget(l);
    	controller.addTarget(new FloatingBlockTarget(block));
    }
    
    private Block createTarget(Location l) {
		int x = l.getBlockX();
		int y = l.getBlockY()+3;
		int z = l.getBlockZ();

    	Block block = createNewBlock(controller.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	blockManipulationUtil .createNewWool(controller.getWorld(), x, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x+1, y, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x-1, y, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x, y-1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(controller.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	return block;
    }
    
    private Block createNewBlock(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	return block;
    }

}
