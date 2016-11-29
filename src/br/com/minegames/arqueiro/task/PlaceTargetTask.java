package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.minigame.domain.MyCloudCraftGame;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.service.TargetService;

public class PlaceTargetTask extends BukkitRunnable {
	
	private GameController controller;
	private TargetService targetService;
	private ConfigService configService = ConfigService.getInstance();
	
	public PlaceTargetTask(GameController controller) {
		this.controller = controller;
		this.targetService = new TargetService(controller);
	}
	
    @Override
    public void run() {
    	MyCloudCraftGame game = configService.getMyCloudCraftGame();
    	if(!game.isStarted()) {
    		return;
    	}


    	int configValue = (int) configService.getGameArenaConfig(Constants.MAX_TARGET);
    	
    	//Essa direção vai indicar como criar e destruir os targets
    	FacingDirection facing = configService.getArena().getFacing();
    	Bukkit.getLogger().info("facing: " + facing );
    	if(facing == null) {
    		facing = FacingDirection.NORTH;
    	}
    	
    	if(controller.getTargets().size() >= configValue ) {
    		return;
    	}
    	
    	int index = (controller.getTargets().size() % 2);

    	if( index == 0 ) {
			targetService.createGroundTarget(facing);
    	} else {
    		targetService.createFloatingTarget(facing);
    	}
    	
    }
    

}
