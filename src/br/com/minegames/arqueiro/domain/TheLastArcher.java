package br.com.minegames.arqueiro.domain;

import com.thecraftcloud.domain.GameState;
import com.thecraftcloud.domain.Level;
import com.thecraftcloud.domain.MyCloudCraftGame;

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
