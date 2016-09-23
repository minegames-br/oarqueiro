package br.com.minegames.arqueiro.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.logging.Logger;

public class BowShootListener implements Listener {

	private Game game;

	public BowShootListener(Game plugin) {
		super();
		this.game = plugin;
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		//ItemStack arrow = new ItemStack(Material.ARROW);
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			Logger.log("BowShoot tem arrow: " + player.getInventory().contains(Material.ARROW));
			//player.getInventory().addItem(arrow);
		}
	}
}
