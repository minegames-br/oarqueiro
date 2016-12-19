package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.minigame.domain.GamePlayer;
import com.thecraftcloud.minigame.domain.MyCloudCraftGame;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.SkeletonTarget;

public class SpawnSkeletonTask implements Runnable {

	private GameController controller;
	private Skeleton entity;
	private ConfigService configService = ConfigService.getInstance();
	private Boolean isSpawned = false;

	public SpawnSkeletonTask(GameController game) {
		this.controller = game;
	}

	@Override
	public void run() {

		MyCloudCraftGame game = configService.getMyCloudCraftGame();
		if (!game.isStarted()) {
			return;
		}

		if (!isSpawned) {
			if ((game.getLevel().getLevel() == 9)) {
				this.spawnSkeleton();
				isSpawned = true;
			}
		}

	}

	private void spawnSkeleton() {
		int loc = 1;
		for (GamePlayer gp : controller.getLivePlayers()) {
			Player player = gp.getPlayer();
			Area3D spawnPoint = (Area3D) configService.getGameArenaConfig("arqueiro.player" + loc + ".area");

			int x = (spawnPoint.getPointA().getX() + spawnPoint.getPointB().getX()) / 2;
			int z = (spawnPoint.getPointA().getZ() + spawnPoint.getPointB().getZ()) / 2;
			Location l = null;

			if (this.configService.getArena().getFacing() == FacingDirection.NORTH) {
				l = new Location(player.getWorld(), x, player.getLocation().getBlockY() + 1, z - 15);

			} else if (this.configService.getArena().getFacing() == FacingDirection.EAST) {
				l = new Location(player.getWorld(), x + 15, player.getLocation().getBlockY() + 1, z);
			}

			entity = (Skeleton) configService.getWorld().spawnEntity(l, EntityType.SKELETON);

			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 5));
			entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
			entity.setTarget(player);
			controller.addEntityPlayer(new SkeletonTarget(entity));
			loc++;
		}
	}

}
