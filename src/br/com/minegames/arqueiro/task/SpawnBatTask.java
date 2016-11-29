package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.thecraftcloud.minigame.domain.MyCloudCraftGame;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.TheLastArcher;

public class SpawnBatTask extends BukkitRunnable {
	
	private GameController controller;
	private ConfigService configService = ConfigService.getInstance();
	
	public SpawnBatTask(GameController game) {
		this.controller = game;
	}
	
    @Override
    public void run() {
    	
    	MyCloudCraftGame game = configService.getMyCloudCraftGame();
    	if(!game.isStarted()) {
    		return;
    	}

    	Entity bat = configService.getWorld().spawnEntity( new Location(configService.getWorld(), 470, 15 ,1180) , EntityType.BAT);

    	Skeleton skeleton = (Skeleton)configService.getWorld().spawnEntity( new Location(configService.getWorld(), 470, 4 ,1180) , EntityType.SKELETON);
    	ItemStack i = new ItemStack(Material.BOW, 1);
    	ItemStack a = new ItemStack(Material.ARROW, 64);
    	skeleton.getEquipment().setItemInMainHand(i);
    	skeleton.getEquipment().setItemInOffHand(a);
        
    }
    
}
