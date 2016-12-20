package br.com.minegames.arqueiro.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.minigame.domain.EntityPlayer;
import com.thecraftcloud.minigame.service.ConfigService;
import com.thecraftcloud.minigame.service.PlayerService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.Target;

public class EntityService {

	private GameController controller;
	private ArcherService archerService;
	private PlayerService playerService;
	private ConfigService configService = ConfigService.getInstance();


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
				et = (EntityTarget) z;
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
				et = (EntityTarget) z;
			}
		}
		if (!foundTarget) {
		}
		return et;
	}

	public void killEntityTargets() {
		for (EntityPlayer eTarget : controller.getLivingEntities()) {
			if (eTarget instanceof EntityTarget) {
				this.clearAllEntitys(((EntityTarget) eTarget).getLivingEntity()); // this.killZombie...getZombie
			}
		}
	}

	public void clearAllEntitys(Entity z) {
		EntityTarget et = (EntityTarget) findEntityTarget(z);
		if (et != null) {
			et.setKillPoints(100);
			controller.getLivingEntities().remove(et);

		}
	}

	public void killEntity(Entity z, Player player) {
		EntityTarget et = (EntityTarget) findEntityTarget(z);
		if (et != null) {
			if (player != null) {
				et.kill(player);
				this.playerService.givePoints(player, et.getKillPoints());
				controller.getLivingEntities().remove(et);
			}
		}
	}

	public void explodeEntity(Entity z, Archer archer) {
		EntityTarget et = (EntityTarget) findEntityTarget(z);
		Location loc = z.getLocation();

		if (archer != null && et != null) {
			if (archerService.damageArcherArea(archer)) {
				((Damageable) z).damage(((Damageable) z).getMaxHealth());
				controller.getLivingEntities().remove(et);
				configService.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 2.0F, false, false);
			} else {
				archerService.destroyBase(archer, loc.getBlockX());
			}

		}

	}

}
