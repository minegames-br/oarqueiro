package br.com.minegames.arqueiro.task;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.TheLastArcher;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.core.logging.MGLogger;
import br.com.minegames.core.util.Utils;
import br.com.minegames.gamemanager.domain.MyCloudCraftGame;

public class SpawnZombieTask extends BukkitRunnable {

	private GameController controller;

	public SpawnZombieTask(GameController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {

		MyCloudCraftGame game = controller.getMyCloudCraftGame();
		if (!game.isStarted()) {
			return;
		}

		int configValue = (Integer)controller.getGameArenaConfig(Constants.MAX_ZOMBIE_SPAWNED_PER_PLAYER);
		if (controller.getLivingTargets().size() < configValue ) {
			Zombie zombie = spawnZombie();
		}
	}

	private Zombie spawnZombie() {
		Location l = controller.getRandomSpawnLocationForGroundEnemy();
		l.setY( l.getBlockY() + 1);
		Zombie entity = (Zombie) controller.getWorld().spawnEntity(l, EntityType.ZOMBIE);
		int index = new Random().nextInt(controller.getLivePlayers().size());
		Archer archer = (Archer) controller.getLivePlayers().toArray()[index];
		entity.setTarget(archer.getPlayer());
		controller.addEntityTarget(new ZombieTarget(entity));

		if (!entity.isBaby()) {
			if ((this.controller.getMyCloudCraftGame().getLevel().getLevel() % 2) == 0) {
				entity.addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED, 10000, controller.getMyCloudCraftGame().getLevel().getLevel()/2));
			}
		}
		return entity;
	}

}
