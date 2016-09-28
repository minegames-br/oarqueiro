package br.com.minegames.arqueiro.task;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;
import br.com.minegames.util.BlockManipulationUtil;

public class PlaceTargetTask extends BukkitRunnable {
	
	private GameController game;
	
	public PlaceTargetTask(GameController game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	if(game.getTargets().size() >= game.getMaxTarget() ) {
    		return;
    	}
    	
    	int index = 6;
    	
    	for(Target target: game.getTargets()) {
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
    	Location l = game.getRandomSpawnLocationForGroundTarget();
    	Block block = createTarget(l);
    	BlockManipulationUtil.createNewWool(game.getWorld(), l.getBlockX(), l.getBlockY()-2, l.getBlockZ(), DyeColor.WHITE );
    	game.addTarget(new GroundBlockTarget(block));
    }

    /**
     * Criar um alvo que fica na parede
     * @return
     */
    private void createWallTarget() {
    	Location l = game.getRandomSpawnLocationForWallTarget();
    	Block block = createTarget(l);
    	game.addTarget(new WallBlockTarget(block));
    }

    /**
     * Criar um alvo que fica fora da parede e longe do chão
     * @return
     */
    private void createFloatingTarget() {
    	Location l = game.getRandomSpawnLocationForFloatingTarget();
    	Block block = createTarget(l);
    	game.addTarget(new FloatingBlockTarget(block));
    }
    
    private Block createTarget(Location l) {
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
    	return block;
    }
    
    private Block createNewBlock(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	return block;
    }

}
