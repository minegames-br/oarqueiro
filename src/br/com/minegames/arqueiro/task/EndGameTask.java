package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.arqueiro.GameState;
import br.com.minegames.util.Utils;

public class EndGameTask extends BukkitRunnable {
	
	private Game game;
	private int countDown = 30;
	
	public EndGameTask(Game game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	//Terminar o jogo caso o tempo termine
    	if(countDown == 0 && game.isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - Time is Over"));
            game.setGameState(GameState.GAMEOVER);
    		game.endGame();
    	} else {
    		countDown --;
    	}
    	
    	//Terminar o jogo caso não tenha mais jogadores
    	if( game.getPlayers().size() == 0  && game.isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - No more players"));
            game.setGameState(GameState.GAMEOVER);
    		game.endGame();
    	}
    	
    }

}
