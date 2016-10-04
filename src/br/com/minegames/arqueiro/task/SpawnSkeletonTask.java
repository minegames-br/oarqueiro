package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Game;

public class SpawnSkeletonTask extends BukkitRunnable {
	
	private GameController controller;
	
	public SpawnSkeletonTask(GameController game) {
		this.controller = game;
	}
	
    @Override
    public void run() {
    	
    	Game game = controller.getGame();
    	if(!game.isStarted()) {
    		return;
    	} 
    	
    	//Pegar uma Location aleatória na área de spawn
    	Location l = controller.getRandomSpawnLocationForGroundEnemy();
    	
    	//Fazer spawn do skeleton
    	Skeleton entity = (Skeleton)controller.getWorld().spawnEntity( l , EntityType.SKELETON);
    	
    	//Colocar um Arco e Flechas na mão do Skeleton
    	ItemStack i = new ItemStack(Material.BOW, 1);
    	ItemStack a = new ItemStack(Material.ARROW, 32);
    	entity.getEquipment().setItemInMainHand(i);
    	entity.getEquipment().setItemInOffHand(a);
    }
    
}
