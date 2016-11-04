package br.com.minegames.arqueiro.domain;

import br.com.minegames.gamemanager.domain.GameState;
import br.com.minegames.gamemanager.domain.Level;
import br.com.minegames.gamemanager.domain.MyCloudCraftGame;

public class TheLastArcher extends MyCloudCraftGame {

	public TheLastArcher() {

		//mudar o state do jogo para esperar jogadores entrarem
		this.state = GameState.WAITING;
		this.level = new Level();
		
	}



}
