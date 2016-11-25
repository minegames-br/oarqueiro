package br.com.minegames.arqueiro.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.service.ArcherService;
import br.com.minegames.arqueiro.service.EntityService;

public class PlayerDeath implements Listener {

    private GameController controller;
    private EntityService entityService;
    private ArcherService archerService;

    public PlayerDeath(GameController controller) {
        this.controller = controller;
        this.entityService = new EntityService(controller);
        this.archerService = new ArcherService(controller);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
    	
        event.getDrops().clear();
        event.setDroppedExp(0);
        
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent)event;
			Player dead = (Player)playerDeathEvent.getEntity();
			this.archerService.killPlayer(dead);
		} else {
			Entity entity = event.getEntity();
			if(entity instanceof Entity) {
				Entity z = (Entity)entity;
				if(((LivingEntity) z).getKiller() == null) {
				} else {
				}
				this.entityService.killEntity(z);
			}
		}
    }

}