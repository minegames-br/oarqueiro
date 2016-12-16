package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.minigame.domain.MyCloudCraftGame;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.arqueiro.service.LocalService;

public class SpawnZombieTask implements Runnable {

	private GameController controller;
	private LocalService localService;
	private ConfigService configService = ConfigService.getInstance();

	public SpawnZombieTask(GameController controller) {
		this.controller = controller;
		this.localService = new LocalService(controller);
	}

	@Override
	public void run() {
	

		MyCloudCraftGame game = configService.getMyCloudCraftGame();
		if (!game.isStarted()) {
			return;
		}

		int configValue = (Integer)configService.getGameArenaConfig(Constants.MAX_ZOMBIE_SPAWNED_PER_PLAYER);
		Bukkit.getConsoleSender().sendMessage(Utils.color("&5[SPAWNZOMBIE] "));
		Bukkit.getConsoleSender().sendMessage(Utils.color("&5[GET LIVING ENTITIES] " + controller.getLivingEntities().size() ));
		Bukkit.getConsoleSender().sendMessage(Utils.color("&5[SPAWNZOMBIE -CONFIGVALUE] " + configValue));
		if (controller.getLivingEntities().size() < configValue ) {
			Zombie zombie = spawnZombie();
		}
	}

	private Zombie spawnZombie() {
		Location l = localService.getRandomSpawnLocationForGroundEnemy();
		l.setY( l.getBlockY() + 1);
		Zombie entity = (Zombie) configService.getWorld().spawnEntity(l, EntityType.ZOMBIE);
		int index = new Random().nextInt(controller.getLivePlayers().size());
		Archer archer = (Archer) controller.getLivePlayers().toArray()[index];
		entity.setTarget(archer.getPlayer());
		controller.addEntityPlayer(new ZombieTarget(entity));
		Bukkit.getConsoleSender().sendMessage(Utils.color("&5[SPAWNZOMBIE] " + l));

		if (!entity.isBaby()) {
			if ((this.configService.getMyCloudCraftGame().getLevel().getLevel() % 2) == 0) {
				entity.addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED, 10000, configService.getMyCloudCraftGame().getLevel().getLevel()/2));
			}
		}
		return entity;
	}

}
