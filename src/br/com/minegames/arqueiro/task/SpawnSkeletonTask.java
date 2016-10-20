package br.com.minegames.arqueiro.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.SkeletonTarget;
import br.com.minegames.core.logging.Logger;

public class SpawnSkeletonTask extends BukkitRunnable {

	private GameController controller;
	private Skeleton entity;

	public SpawnSkeletonTask(GameController game) {
		this.controller = game;
	}

	@Override
	public void run() {

		Game game = controller.getGame();
		if (!game.isStarted()) {
			return;
		}

		if ((controller.getGame().getLevel().getLevel() == 2)) {
			Logger.log("spawnSkeleton");
			this.entity = spawnSkeleton();
		}

	}

	private Skeleton spawnSkeleton() {
		Object aList[] = controller.getLivePlayers().toArray();

		for (int i = 0; i < aList.length; i++) {
			Archer a = (Archer) aList[i];

			if (i == 0) {
				Location l = new Location(this.controller.getWorld(), 460, 5, 1175);
				entity = (Skeleton) controller.getWorld().spawnEntity(l, EntityType.SKELETON);
			} else if (i == 1) {
				Location l = new Location(this.controller.getWorld(), 472, 5, 1175);
				entity = (Skeleton) controller.getWorld().spawnEntity(l, EntityType.SKELETON);
			} else if (i == 2) {
				Location l = new Location(this.controller.getWorld(), 480, 5, 1175);
				entity = (Skeleton) controller.getWorld().spawnEntity(l, EntityType.SKELETON);
			} else if (i == 3) {
				Location l = new Location(this.controller.getWorld(), 490, 5, 1175);
				entity = (Skeleton) controller.getWorld().spawnEntity(l, EntityType.SKELETON);
			}

			// dar equipamentos para o Skeleton
			entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
			entity.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			entity.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			entity.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			entity.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			entity.setTarget(a.getPlayer());
			controller.addEntityTarget(new SkeletonTarget(entity));
		}
		return entity;
	}
}
