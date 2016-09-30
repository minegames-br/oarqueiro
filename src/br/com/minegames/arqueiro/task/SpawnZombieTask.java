package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.util.Utils;
import zedly.zenchantments.Utilities;

public class SpawnZombieTask extends BukkitRunnable {
	
	private GameController controller;
	
	public SpawnZombieTask(GameController controller) {
		this.controller = controller;
	}
	
    @Override
    public void run() {

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
    	entity.setTarget(archer.getPlayer());
    	controller.addEntityTarget(new ZombieTarget(entity));
		Utilities.addPotion(entity, PotionEffectType.SPEED, 10000, controller.getGame().getLevel().getLevel());
    	return entity;
    }
    
    public static void main(String args[]) {
    	for(int i = 0; i < 100; i++) {
    		System.out.println(new Random().nextInt(4));
    	}
    }
}
