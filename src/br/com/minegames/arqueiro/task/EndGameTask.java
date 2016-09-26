package br.com.minegames.arqueiro.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.util.Utils;

public class EndGameTask extends BukkitRunnable {
	
	private GameController controller;
	
	public EndGameTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {

    	//Terminar o jogo após o 10 Nível
    	if(controller.getGame().getLevel().getLevel() >= 11 && controller.getGame().isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - Time is Over"));
            controller.endGame();
    	}
    	
    	//Terminar o jogo caso não tenha mais jogadores
    	if( controller.getLivePlayers().size() == 0  && controller.getGame().isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - No more players"));
            controller.endGame();
    	}
    	
    }

}
