package br.com.minegames.arqueiro.task;

import java.util.Iterator;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.domain.EntityPlayer;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.service.EntityService;

public class ExplodeZombieTask extends BukkitRunnable {
	
	private GameController controller;
	private EntityService entityService;
	
	public ExplodeZombieTask(GameController controller) {
		this.controller = controller;
		this.entityService = new EntityService(controller);
	}
	
    @Override
    public void run() {
    	
    	//Explodir o zombie caso ele esteja na área de algum jogador
    	//A explosão irá causar dano aos blocks de cerca perto do zombie
    	Iterator<EntityPlayer> iterator = this.controller.getLivingEntities().iterator();
    	while(iterator.hasNext()) {
    		EntityPlayer t = iterator.next();
    		if(t instanceof EntityTarget) {
    			EntityTarget e = (EntityTarget)t;
    			Entity entity = e.getLivingEntity();
    			if( controller.shouldExplodeZombie(entity.getLocation())){
    				//Bukkit.broadcastMessage("Zombie is in region");
    				this.entityService.killEntity(entity);
    			}
    		}
    	}
    	
    }

}
