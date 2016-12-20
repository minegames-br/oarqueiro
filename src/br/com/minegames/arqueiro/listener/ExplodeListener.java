package br.com.minegames.arqueiro.listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.core.domain.Local;
import com.thecraftcloud.minigame.domain.GamePlayer;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.service.EntityService;

public class ExplodeListener implements Listener {

	private GameController controller;
	private EntityService entityService;
	private ConfigService configService = ConfigService.getInstance();

	public ExplodeListener(GameController controller) {
		super();
		this.controller = controller;
		this.entityService = new EntityService(controller);
	}

	@EventHandler
	public void onInteract(EntityInteractEvent event) {

		Entity entity = event.getEntity();

		for (GamePlayer gp : controller.getLivePlayers()) {
			Archer archer = (Archer) gp;

			if (event.getBlock().getType() == Material.IRON_PLATE || event.getBlock().getType() == Material.GOLD_PLATE
					|| event.getBlock().getType() == Material.STONE_PLATE
					|| event.getBlock().getType() == Material.WOOD_PLATE)
				canExplode(archer, entity);

		}

	}

	private void canExplode(Archer archer, Entity entity) {
		int entity_X = entity.getLocation().getBlockX();
		int entity_Z = entity.getLocation().getBlockZ();
		Local point_A = archer.getArea().getPointA();
		Local point_B = archer.getArea().getPointB();

		if (this.configService.getArena().getFacing() == FacingDirection.EAST
				|| this.configService.getArena().getFacing() == FacingDirection.WEST) {
			if (entity_Z <= point_A.getZ() && entity_Z >= point_B.getZ()) {
				this.entityService.explodeEntity(entity, archer);
			}

		} else if (this.configService.getArena().getFacing() == FacingDirection.NORTH
				|| this.configService.getArena().getFacing() == FacingDirection.SOUTH) {
			if (entity_X <= point_A.getX() && entity_X >= point_B.getX()) {
				this.entityService.explodeEntity(entity, archer);
			}

		}
	}

}