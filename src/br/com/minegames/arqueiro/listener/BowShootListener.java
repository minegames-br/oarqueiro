package br.com.minegames.arqueiro.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.logging.MGLogger;

public class BowShootListener implements Listener {

	private GameController controller;

	public BowShootListener(GameController controller) {
		super();
		this.controller = controller;
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		//ItemStack arrow = new ItemStack(Material.ARROW);
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			controller.shootArrows(player);
		}
	}
	
}
