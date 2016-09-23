package br.com.minegames.arqueiro.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import br.com.minegames.arqueiro.Game;

public class BowShootListener implements Listener {

	private Game game;	
	
	public BowShootListener(Game plugin) {
		super();
		this.game = plugin;
	}
	
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			player.getInventory().addItem(arrow);
		}
	 }
}
