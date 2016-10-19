package br.com.minegames.arqueiro.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.util.Utils;

public class PlayerMove implements Listener {

    public PlayerMove(GameController plugin) {
		super();
		this.controller = plugin;
	}

	private GameController controller;

    @EventHandler
    public void onJoin(PlayerMoveEvent event) {

    	if(controller.getGame().isStarted()) {
    		if( event.getTo().getBlockZ() >= 1169 ) {
    			Utils.shootFirework(event.getTo());
    			event.setCancelled(true);
    		}
    	}
        
    }

}