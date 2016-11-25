package br.com.minegames.arqueiro.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.plugin.service.ConfigService;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.service.TargetService;

public class PlaceMovingTargetTask extends BukkitRunnable {
	
	private GameController controller;
	private TargetService targetService;
	private ConfigService configService = ConfigService.getInstance();
	
	public PlaceMovingTargetTask(GameController controller) {
		this.controller = controller;
		this.targetService = new TargetService(controller);
	}
	
    @Override
    public void run() {

    	//mover os alvos criados um bloco para baixo
    	targetService.moveTargets();

    	int configValue = (int)configService.getGameArenaConfig(Constants.MAX_MOVING_TARGET);
    	if(controller.getMovingTargets().size() >= configValue ) {
    		return;
    	}
    	
    	//se não tiver um moving target criar um
    	if(controller.getMovingTargets().size() < configValue) {
    		targetService.createVerticalMovingTarget();
    	}
    	
    }
    

}
