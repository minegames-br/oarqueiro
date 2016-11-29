package br.com.minegames.arqueiro.domain;

import com.thecraftcloud.minigame.domain.GameState;
import com.thecraftcloud.minigame.domain.Level;
import com.thecraftcloud.minigame.domain.MyCloudCraftGame;

public class TheLastArcher extends MyCloudCraftGame {

	public TheLastArcher() {

		//mudar o state do jogo para esperar jogadores entrarem
		this.state = GameState.WAITING;
		this.level = new Level();
		
	}

	@Override
	public boolean hasLevels() {
		return true;
	}


}
