package br.com.minegames.arqueiro.task;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.Arena;
import com.thecraftcloud.core.logging.MGLogger;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.FastMovingTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;

public class PlaceMovingTargetTask extends BukkitRunnable {
	
	private GameController controller;
	
	public PlaceMovingTargetTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {

    	//mover os alvos criados um bloco para baixo
    	moveTargets();

    	int configValue = (int)controller.getGameArenaConfig(Constants.MAX_MOVING_TARGET);
    	if(controller.getMovingTargets().size() >= configValue ) {
    		return;
    	}
    	
    	//se não tiver um moving target criar um
    	if(controller.getMovingTargets().size() < configValue) {
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
    	target.setMaxMoves(getMaxMoves());
    	target.setMoves(0);
    	controller.addMovingTarget(target);
    }
    
    private MovingTarget createTarget(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	
       	FastMovingTarget vmTarget = new FastMovingTarget(block);
       	vmTarget.setMaxMoves(getMaxMoves());
       	vmTarget.setMoves(0);
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
			mt.setMoves(mt.getMoves()+1);
    		
    		if( mt.getMoves() >= mt.getMaxMoves() ) {
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
	
	private Integer getMaxMoves() {
		int maxMoves = 0;
		
    	Arena arena = (Arena)controller.getArena();
    	Area3D area = (Area3D)controller.getGameArenaConfig(Constants.FLOATING_AREA);
    	
    	maxMoves = Math.abs( area.getPointA().getY() - (area.getPointB().getY()) );
    	
    	return maxMoves;
	}

}
