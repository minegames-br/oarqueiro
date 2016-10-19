package br.com.minegames.arqueiro.task;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;
import br.com.minegames.core.util.BlockManipulationUtil;

public class PlaceTargetTask extends BukkitRunnable {
	
	private GameController controller;
	private GroundBlockTarget groundTarget;
	
	public PlaceTargetTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {
    	Game game = controller.getGame();
    	if(!game.isStarted()) {
    		return;
    	}

    	int configValue = controller.getGameInstance().getConfigIntValue(Constants.MAX_TARGET);
    	if(controller.getTargets().size() >= configValue ) {
    		return;
    	}
    	
    	int index = 6;
    	
    	for(Target target: controller.getTargets()) {
    		if(target instanceof GroundBlockTarget) {
    			index --;
    		} else if(target instanceof FloatingBlockTarget) {
    			index -= 2;
    		} else if(target instanceof WallBlockTarget) {
    			index -= 4;
    		}
    	}

    	if( index == 0 || index == 2 || index == 6) {
			createGroundTarget();
    	} else if( index == 1 ) {
			createFloatingTarget();
    	} else if( index == 3 ) {
			createWallTarget();
    	} else if( index == 5 || index == 4 ) {
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
    	BlockManipulationUtil.createNewWool(controller.getWorld(), l.getBlockX(), l.getBlockY()-2, l.getBlockZ(), DyeColor.WHITE );
    	controller.addTarget(new GroundBlockTarget(block));
    }

    /**
     * Criar um alvo que fica na parede
     * @return
     */
    private void createWallTarget() {
    	/*
    	Location l = controller.getRandomSpawnLocationForWallTarget();
    	Block block = createTarget(l);
    	controller.addTarget(new WallBlockTarget(block));
    	*/
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
		int y = l.getBlockY();
		int z = l.getBlockZ();
		
    	Block block = createNewBlock(controller.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x+1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x-1, y, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	BlockManipulationUtil.createNewWool(controller.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	return block;
    }
    
    private Block createNewBlock(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	return block;
    }

}
