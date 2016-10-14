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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.logging.Logger;

public class TargetHitEvent implements Listener {

	private GameController game;	
	
	public TargetHitEvent(GameController plugin) {
		super();
		this.game = plugin;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
	    if(!(event.getEntity() instanceof Arrow))
	        return;

	    if(!(event.getEntity().getShooter() instanceof Player))
	        return;

	    Logger.log("onProjectileHit");

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
		    
	    	Iterator<Target> it = game.getTargets().iterator();

	    	while(it.hasNext()) {
	    		Target target = it.next();

	            if( target instanceof BlockTarget ) {
	            	
	            	BlockTarget bTarget = (BlockTarget)target;
	            	Location l1 = hit.getLocation();
	            	Location l2 = bTarget.getBlock().getLocation();
	            	           	
	            	if( l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ() ) {
		                game.hitTarget(bTarget, shooter);
	            	}
	            }
		    }
	    	
		    
	    	Iterator<MovingTarget> it2 = game.getMovingTargets().iterator();

	    	while(it2.hasNext()) {
	    		MovingTarget target = it2.next();

            	Location l1 = hit.getLocation();
            	Location l2 = target.getBlock().getLocation();
            	
            	if( l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ() ) {
	                game.hitMovingTarget(target, shooter);
            	}
            }
	    		    	
	    }

	    //Retirar a flecha da arena, mas só após alguns milisegundos depois que ela parar
	    //para que o jogador veja onde ela acertou
	    final Arrow arrow = (Arrow)event.getEntity();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        
        scheduler.runTaskLater(game, new Runnable() {
        	public void run() {
        	    arrow.remove();
        	}
        }, 50L);
	    
	}
	
}

