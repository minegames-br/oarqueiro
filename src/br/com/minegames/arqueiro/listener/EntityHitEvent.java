package br.com.minegames.arqueiro.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.logging.Logger;

public class EntityHitEvent implements Listener {

	private GameController game;	
	
	public EntityHitEvent(GameController plugin) {
		super();
		this.game = plugin;
	}
	
	@EventHandler
	public void onArrowHit(EntityDamageByEntityEvent event){
		
        if ( !(event.getDamager() instanceof Arrow) ){
        	Logger.log("damager not arrow");
        	return;
        }
        
        Arrow arrow = (Arrow) event.getDamager();
        if(!(arrow.getShooter() instanceof Player)) {
        	Logger.log("shooter not player");
        	return;
        }
        
        Player player = (Player) arrow.getShooter();
    	Logger.log("EntityHit tem arrow: " + player.getInventory().contains(Material.ARROW));
	    if (event.getEntity() instanceof Zombie) {
        	Logger.log("entity is zombie");
	    	game.hitZombie((Zombie)event.getEntity(), player);
	    }else{
        	Logger.log("entity not a zombie");
	    }
	    
	}
}
