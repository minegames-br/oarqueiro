package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Game;

public class SpawnSkeletonTask extends BukkitRunnable {
	
	private Game game;
	
	public SpawnSkeletonTask(Game game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	//Pegar uma Location aleatória na área de spawn
    	Location l = game.getRandomSpawnLocationForGroundEnemy();
    	
    	//Fazer spawn do skeleton
    	Skeleton skeleton = (Skeleton)game.getWorld().spawnEntity( l , EntityType.SKELETON);
    	
    	//Colocar um Arco e Flechas na mão do Skeleton
    	ItemStack i = new ItemStack(Material.BOW, 1);
    	ItemStack a = new ItemStack(Material.ARROW, 32);
    	skeleton.getEquipment().setItemInMainHand(i);
    	skeleton.getEquipment().setItemInOffHand(a);
        
    }
    
}
