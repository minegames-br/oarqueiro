package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;

public class SpawnBatTask extends BukkitRunnable {
	
	private GameController game;
	
	public SpawnBatTask(GameController game) {
		this.game = game;
	}
	
    @Override
    public void run() {

    	Entity bat = game.getWorld().spawnEntity( new Location(game.getWorld(), 470, 15 ,1180) , EntityType.BAT);

    	Skeleton skeleton = (Skeleton)game.getWorld().spawnEntity( new Location(game.getWorld(), 470, 4 ,1180) , EntityType.SKELETON);
    	ItemStack i = new ItemStack(Material.BOW, 1);
    	ItemStack a = new ItemStack(Material.ARROW, 64);
    	skeleton.getEquipment().setItemInMainHand(i);
    	skeleton.getEquipment().setItemInOffHand(a);
        
    }
    
}
