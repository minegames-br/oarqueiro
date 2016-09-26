package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.GameState;
import br.com.minegames.util.Utils;

public class PlayerQuit implements Listener {

    public PlayerQuit(GameController game) {
		super();
		this.game = game;
	}

	private GameController game;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6PlayerQuit.onQuit"));
        Player player = event.getPlayer();
        
        if( !game.getGame().isOver() ) {
        	game.removeLivePlayer(player);
        }
        
        //limpar o inventário do jogador
        player.getInventory().clear();
        
        game.sendToLobby(player);
    }

}