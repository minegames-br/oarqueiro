package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.util.Utils;

public class StartCoundDownTask extends BukkitRunnable {
	
	private Game game;
	
	public StartCoundDownTask(Game game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	if( !game.isStarting() ) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Verifying if the countdown can start"));
        	if( game.getPlayers().size() >= game.getMinPlayers() && !game.isStarting() ) {
        		game.startCoundDown();
        	}
    	} else {
    		game.proceedCountdown();
    	}
    }

}
