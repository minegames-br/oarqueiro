package br.com.minegames.arqueiro.domain;

import br.com.minegames.arqueiro.GameState;
import br.com.minegames.arqueiro.domain.level.Level;

public class Game {

	private GameState state;
    private long gameStartTime;
    private Level level;

	public Game() {

		//mudar o state do jogo para esperar jogadores entrarem
		this.state = GameState.WAITING;
		this.level = new Level();
		
	}

	public void shutDown() {
		state = GameState.SHUTDOWN;
	}

	public void start() {
		state = GameState.RUNNING;
		this.gameStartTime = System.currentTimeMillis();
	}

	public boolean isShuttingDown() {
		return this.state.equals(GameState.SHUTDOWN);
	}

	public void startCountDown() {
		this.state = GameState.STARTING;
	}

	public boolean isStarting() {
		return this.state.equals(GameState.STARTING);
	}

	public boolean isStarted() {
		return this.state.equals(GameState.RUNNING);
	}

	public boolean isWaitingPlayers() {
		return this.state.equals(GameState.WAITING) || this.state.equals(GameState.STARTING);	
	}

	public void endGame() {
		this.state = GameState.GAMEOVER;
	}

	public boolean isOver() {
		return this.state.equals(GameState.GAMEOVER);
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void levelUp() {
		this.level = new Level(this.level);
	}


}
