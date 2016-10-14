package br.com.minegames.arqueiro.task;

import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.GameState;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.logging.Logger;

public class LevelUpTask extends BukkitRunnable {
	
	private GameController controller;
	
	public LevelUpTask(GameController game) {
		this.controller = game;
	}
	
    @Override
    public void run() {
    	
    	Game game = controller.getGame();
    	if(!game.isStarted()) {
    		return;
    	}
    	
    	//Aumentar de nível depois de 15 segundos
    	//Caso seja o último nível, terminar o jogo
    	if(controller.getGame().getLevel().lifeTime() >= 5000) {
    		if(controller.isLastLevel()) {
    			controller.endGame();
    		} else {
        		this.controller.getGame().setGameState(GameState.LEVELUP);
        		controller.levelUp();
    		}
    	}
    	
    }

}
