package br.com.minegames.arqueiro.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.service.TargetService;

public class TargetHitEvent implements Listener {

	private GameController controller;	
	private TargetService targetService;
	
	public TargetHitEvent(GameController controller) {
		super();
		this.controller = controller;
		this.targetService = new TargetService(controller);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!controller.getMyCloudCraftGame().isStarted()) {
			return;
		}
	    if(!(event.getEntity() instanceof Arrow))
	        return;

	    if(!(event.getEntity().getShooter() instanceof Player))
	        return;

	    
	    
	    if (event.getEntity() instanceof Arrow){
		    Arrow arrow = (Arrow)event.getEntity();
		    
		    Player shooter = (Player) arrow.getShooter();
		    World world = arrow.getWorld();
		    BlockIterator bi = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
		    Block hit = null;

		    while(bi.hasNext()) {
		    	hit = bi.next();
		    	if(!hit.getType().equals(Material.AIR)) //Grass/etc should be added probably since arrows doesn't collide with them
		    	{
		    		break;
		    	}
		    }
		    
		    Target target = targetService.hasHittenTarget(hit);
		    if(target != null) {
		    	targetService.hitTarget(shooter, target);
		    }
	    		    	
	    }

	    //Retirar a flecha da arena, mas só após alguns milisegundos depois que ela parar
	    //para que o jogador veja onde ela acertou
	    final Arrow arrow = (Arrow)event.getEntity();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        
        scheduler.runTaskLater(controller, new Runnable() {
        	public void run() {
        	    arrow.remove();
        	}
        }, 50L);
	    
	}
	
}

