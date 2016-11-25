package br.com.minegames.arqueiro.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.service.ArcherService;

public class BowShootListener implements Listener {

	private GameController controller;
	private ArcherService archerService;

	public BowShootListener(GameController controller) {
		super();
		this.controller = controller;
		this.archerService = new ArcherService(controller);
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		if(!controller.getMyCloudCraftGame().isStarted()) {
			return;
		}
		//ItemStack arrow = new ItemStack(Material.ARROW);
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			archerService.shootArrows(player);
		}
	}
	
}
