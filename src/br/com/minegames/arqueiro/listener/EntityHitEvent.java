package br.com.minegames.arqueiro.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.logging.Logger;

public class EntityHitEvent implements Listener {

	private GameController game;	
	
	public EntityHitEvent(GameController plugin) {
		super();
		this.game = plugin;
	}
	
	@EventHandler
	public void onArrowHit(EntityDamageByEntityEvent event){
		
        if ( !(event.getDamager() instanceof Arrow) ){
        	return;
        }
        
        Arrow arrow = (Arrow) event.getDamager();
        if(!(arrow.getShooter() instanceof Player)) {
        	Logger.log("shooter not player");
        	return;
        }
        
        Player player = (Player) arrow.getShooter();
	    if (event.getEntity() instanceof Entity) {
        	Logger.log("entity is mob");
	    	game.hitEntity(event.getEntity(), player);
	    }else{
        	Logger.log("entity not a mob");
	    }
	    
	}
}
