package br.com.minegames.arqueiro.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.thecraftcloud.core.util.Utils;

import br.com.minegames.arqueiro.GameController;

public class PlayerMove implements Listener {

    public PlayerMove(GameController plugin) {
		super();
		this.controller = plugin;
	}

	private GameController controller;

    @EventHandler
    public void onJoin(PlayerMoveEvent event) {

    	/**
    	 * NAO ME LEMBRO PORQUE FIZ ISSO. ACHO QUE É PARA NAO PERMITIR O PLAYER SAIR DA SUA AREA
    	if(controller.getMyCloudCraftGame().isStarted()) {
    		if( event.getTo().getBlockZ() >= 1169 ) {
    			Utils.shootFirework(event.getTo());
    			event.setCancelled(true);
    		}
    	}
    	 */
        
    }

}