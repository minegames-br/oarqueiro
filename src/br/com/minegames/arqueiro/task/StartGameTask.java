package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.util.Utils;

public class StartGameTask extends BukkitRunnable {
	
	private GameController controller;
	
	public StartGameTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {

    	int minPlayers = controller.getGameDelegate().getGlobalConfig(Constants.MIN_PLAYERS).getIntValue();
    	int maxPlayers = controller.getGameDelegate().getGlobalConfig(Constants.MAX_PLAYERS).getIntValue();
    	
    	Bukkit.getLogger().info("minPlayers: " + minPlayers);
    	Bukkit.getLogger().info("maxPlayers: " + maxPlayers);
    	
    	if( controller.getLivePlayers().size() == maxPlayers && controller.getGame().isWaitingPlayers()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Max number of players achieved. Starting game."));
            controller.startGameEngine();
    	} else if ( (controller.getLivePlayers().size() >= minPlayers)
    			&& controller.getCountDown() == 0 && controller.getGame().isWaitingPlayers() ) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Min number of players achieved. Countdown 0. Starting game."));
            controller.startGameEngine();
    	}
    	
    	
    }

}
