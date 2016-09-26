package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.util.Utils;

public class SpawnZombieTask extends BukkitRunnable {
	
	private GameController game;
	
	public SpawnZombieTask(GameController game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	if(game.getLivingTargets().size() < game.getMaxZombieSpawned() ) {
    		Bukkit.getConsoleSender().sendMessage(Utils.color("&6Spawning Zombie"));
    		spawnZombie();
    	}
        
    }
    
    private void spawnZombie() {
    	Location l = game.getRandomSpawnLocationForGroundEnemy();
    	Zombie entity = (Zombie)game.getWorld().spawnEntity( l , EntityType.ZOMBIE);
    	
    	int index = new Random().nextInt(game.getLivePlayers().size());
    	Archer archer = (Archer)game.getLivePlayers().toArray()[index];
    	entity.setTarget(archer.getPlayer());
    	game.addEntityTarget(new ZombieTarget(game, entity));
    }
    
    public static void main(String args[]) {
    	for(int i = 0; i < 100; i++) {
    		System.out.println(new Random().nextInt(4));
    	}
    }
}
