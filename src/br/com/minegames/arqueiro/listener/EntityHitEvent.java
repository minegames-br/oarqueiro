package br.com.minegames.arqueiro.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.service.EntityService;

public class EntityHitEvent implements Listener {

	private GameController controller;	
	private EntityService entityService;
	
	public EntityHitEvent(GameController controller) {
		super();
		this.controller = controller;
		this.entityService = new EntityService(controller);
	}
	
	@EventHandler
	public void onArrowHit(EntityDamageByEntityEvent event){
		if(!controller.getMyCloudCraftGame().isStarted()) {
			return;
		}
		
        if ( !(event.getDamager() instanceof Arrow) ){
        	return;
        }
        
        Arrow arrow = (Arrow) event.getDamager();
        if(!(arrow.getShooter() instanceof Player)) {
        	return;
        }
        
        Player player = (Player) arrow.getShooter();
	    if (event.getEntity() instanceof Entity) {
	    	entityService.hitEntity(event.getEntity(), player);
	    }else{
	    }
	    
	}
}
