package br.com.minegames.arqueiro.task;

import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;

public class StartCoundDownTask extends BukkitRunnable {
	
	private GameController controller;
	
	public StartCoundDownTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {

    	int configValue = controller.getGameInstance().getConfigIntValue(Constants.MIN_PLAYERS);
    	if( !controller.getGame().isStarting() ) {
        	if( controller.getLivePlayers().size() >= configValue && !controller.getGame().isStarting() ) {
        		controller.startCoundDown();
        	}
    	} else {
    		controller.proceedCountdown();
    	}
    }

}
