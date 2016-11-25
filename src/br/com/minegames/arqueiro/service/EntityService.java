package br.com.minegames.arqueiro.service;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.thecraftcloud.domain.EntityPlayer;
import com.thecraftcloud.plugin.service.PlayerService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.Target;

public class EntityService {
	
	private GameController controller;
	private ArcherService archerService;
	private PlayerService playerService;
	
	public EntityService(GameController controller) {
		this.controller = controller;
		this.archerService = new ArcherService(controller);
		this.playerService = new PlayerService(controller);
	}
	
	public void hitEntityTarget(Target target, Player shooter) {
		target.hitTarget2(shooter);
	}

	public void hitEntity(Entity entity, Player player) {
		EntityTarget target = this.findEntityTarget(entity);
		target.setKiller(player);
	}

	public EntityTarget findEntityTargetByZombie(Zombie zombie) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityPlayer z : controller.getLivingEntities()) {
			if (z.getLivingEntity().equals(zombie)) {
				foundTarget = true;
				et = (EntityTarget)z;
			}
		}
		if (!foundTarget) {
		}
		return et;
	}

	public EntityTarget findEntityTarget(Entity entity) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityPlayer z : controller.getLivingEntities()) {
			if (z.getLivingEntity().equals(entity)) {
				foundTarget = true;
				et = (EntityTarget)z;
			}
		}
		if (!foundTarget) {
		}
		return et;
	}

	public void killEntityTargets() {
		for (EntityPlayer eTarget : controller.getLivingEntities()) {
			if (eTarget instanceof EntityTarget) {
				this.killEntity(((EntityTarget) eTarget).getLivingEntity()); // this.killZombie...getZombie
			}
		}
	}

	public void killEntity(Entity z) {
		EntityTarget et = (EntityTarget) findEntityTarget(z);
		Location loc = z.getLocation();
		if (et != null) {
			if (et.getKiller() != null) {
				Player player = et.getKiller();
				this.playerService.givePoints(player, et.getKillPoints());
				controller.getLivingEntities().remove(et);
			} else {
				if (archerService.damageArcherArea(z)) {
					((Damageable) z).damage(((Damageable) z).getMaxHealth());
					controller.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 2.0F, false, false);
					controller.getLivingEntities().remove(et);
				} else {
					archerService.destroyBase(loc.getBlockX());
				}
			}
		}
	}

}
