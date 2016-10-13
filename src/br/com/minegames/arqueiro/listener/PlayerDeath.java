package br.com.minegames.arqueiro.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.logging.Logger;

public class PlayerDeath implements Listener {

    private GameController game;

    public PlayerDeath(GameController plugin) {
        this.game = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
    	
        event.getDrops().clear();
        event.setDroppedExp(0);
        
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent)event;
			Player dead = (Player)playerDeathEvent.getEntity();
			Logger.log("player: " + dead.getName() + " died.");
			game.killPlayer(dead);
		} else {
			Entity entity = event.getEntity();
			if(entity instanceof Entity) {
				Entity z = (Entity)entity;
				if(((LivingEntity) z).getKiller() == null) {
					Logger.log("Killer está null");
				} else {
					Logger.log(((LivingEntity) z).getKiller() + " " + ((LivingEntity) z).getKiller().getName() );
					game.killEntity(z);
				}
			}
		}
    }

}