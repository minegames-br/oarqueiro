package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.logging.Logger;
import br.com.minegames.util.Utils;

public class SpawnZombieTask extends BukkitRunnable {
	
	private GameController controller;
	
	public SpawnZombieTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {
    	
    	Game game = controller.getGame();
    	if(!game.isStarted()) {
    		return;
    	}

    	if(controller.getLivingTargets().size() < controller.getMaxZombieSpawned() ) {
    		Bukkit.getConsoleSender().sendMessage(Utils.color("&6Spawning Zombie"));
    		Zombie zombie = spawnZombie();
    	}
    }
    
    private Zombie spawnZombie() {
    	Location l = controller.getRandomSpawnLocationForGroundEnemy();
    	Zombie entity = (Zombie)controller.getWorld().spawnEntity( l , EntityType.ZOMBIE);
    	int index = new Random().nextInt(controller.getLivePlayers().size());
    	Archer archer = (Archer)controller.getLivePlayers().toArray()[index];
    	Logger.log("index = " + index + " live players: " + controller.getLivePlayers().size() + archer.getPlayer().getName());
    	entity.setTarget(archer.getPlayer());
    	controller.addEntityTarget(new ZombieTarget(entity));
    	 	
    	if( !entity.isBaby() ) {
    		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, controller.getGame().getLevel().getLevel()));
    	}
    	return entity;
    }
    
}
