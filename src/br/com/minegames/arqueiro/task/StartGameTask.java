package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.util.Utils;

public class StartGameTask extends BukkitRunnable {
	
	private GameController game;
	
	public StartGameTask(GameController game) {
		this.game = game;
	}
	
    @Override
    public void run() {

        //Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Verifying if the game can start"));
    	if( game.getLivePlayers().size() == game.getMaxPlayers() && game.getGame().isWaitingPlayers()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Max number of players achieved. Starting game."));
    		game.startGameEngine();
    	} else if ( (game.getLivePlayers().size() >= game.getMinPlayers())
    			&& game.getCoundDown() == 0 && game.getGame().isWaitingPlayers() ) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6StartGameTask - Min number of players achieved. Countdown 0. Starting game."));
    		game.startGameEngine();
    	}
    	
    	
    }

}
