package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.arqueiro.GameState;
import br.com.minegames.util.Utils;

public class PlayerQuit implements Listener {

    public PlayerQuit(Game game) {
		super();
		this.game = game;
	}

	private Game game;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6PlayerQuit.onQuit"));
        Player player = event.getPlayer();
        
        if(! (game.getGameState().equals(GameState.GAMEOVER) ) ) {
        	game.removePlayer(player);
        }
        
        //limpar o inventário do jogador
        player.getInventory().clear();
        
        game.sendToLobby(player);
    }

}