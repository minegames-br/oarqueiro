package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.ArcherBow;
import br.com.minegames.arqueiro.domain.target.SkeletonTarget;
import br.com.minegames.core.domain.Area3D;
import br.com.minegames.core.logging.MGLogger;
import br.com.minegames.gamemanager.domain.GamePlayer;
import br.com.minegames.gamemanager.domain.MyCloudCraftGame;

public class SpawnSkeletonTask implements Runnable {

	private GameController controller;
	private Skeleton entity;

	public SpawnSkeletonTask(GameController game) {
		this.controller = game;
	}

	@Override
	public void run() {

		MyCloudCraftGame game = controller.getMyCloudCraftGame();
		if (!game.isStarted()) {
			return;
		}

		if ((game.getLevel().getLevel() == 9)) {
			MGLogger.debug("spawnSkeleton");
			this.spawnSkeleton();
		}

	}

	private void spawnSkeleton() {
		// preparar Score Board
		int loc = 1;
		for (GamePlayer gp : controller.getLivePlayers()) {
			Player player = gp.getPlayer();
			//this.world = player.getWorld();
			Area3D spawnPoint = (Area3D)controller.getGameArenaConfig("arqueiro.player" + loc + ".area");
			
			int x = (spawnPoint.getPointA().getX() + spawnPoint.getPointB().getX()) / 2; 
			int z = (spawnPoint.getPointA().getZ() + spawnPoint.getPointB().getZ()) / 2; 
			
			Location l = new Location(player.getWorld(), x, player.getLocation().getBlockY()+1, z);
			
			entity = (Skeleton) controller.getWorld().spawnEntity(l, EntityType.SKELETON);

			// dar equipamentos para o Skeleton
			entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
			entity.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			entity.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			entity.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			entity.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			entity.setTarget(player);
			controller.addEntityTarget(new SkeletonTarget(entity));
			loc++;
		}
	}
}
