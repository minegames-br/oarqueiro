package br.com.minegames.arqueiro.task;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.FastMovingTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;

public class PlaceMovingTargetTask extends BukkitRunnable {
	
	private GameController controller;
	
	public PlaceMovingTargetTask(GameController game) {
		this.controller = game;
	}
	
    @Override
    public void run() {

    	Game game = controller.getGame();
    	if(!game.isStarted()) {
    		return;
    	}
    	
    	//mover os alvos criados um bloco para baixo
    	moveTargets();

    	int configValue = controller.getConfigIntValue(Constants.MAX_MOVING_TARGET);
    	if(controller.getMovingTargets().size() >= configValue ) {
    		return;
    	}
    	
    	//se não tiver um moving target criar um
    	if(controller.getMovingTargets().size() == 0) {
    		createVerticalMovingTarget();
    	}
    	
    }
    
    /**
     * Criar um alvo que "cai" do teto ao chão
     * @return
     */
    private void createVerticalMovingTarget() {
    	Location l = controller.getRandomSpawnLocationForFloatingTarget();
    	MovingTarget target = createTarget(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), Material.BEACON );
    	controller.addMovingTarget(target);
    }
    
    private MovingTarget createTarget(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	
       	FastMovingTarget vmTarget = new FastMovingTarget(block);
       	vmTarget.setOldBlockType(block.getType());
       	return vmTarget;
    }
    
    /**
     * Mover todos os alvos verticais um bloco para baixo
     */
    private void moveTargets() {
    	Block block;
    	
    	Iterator<MovingTarget> it = this.controller.getMovingTargets().iterator();
    	
    	while(it.hasNext()) {
    		MovingTarget mt = it.next();
    		Material oldType = mt.getOldBlockType();
    		Location l = mt.getBlock().getLocation();
    		l.getBlock().setType(Material.AIR);
    		int y = l.getBlockY() - 1;
    		if( y <= 4 ) {
    			destroyTarget(mt);
    		} else {
        		l.setY(y);
        		block = l.getBlock();
        		block.setType(Material.BEACON);
        		mt.setBlock(block);
    		}
    		
    	}
    }

	private void destroyTarget(MovingTarget mt) {
		this.controller.destroyMovingTarget(mt);
	}

}
