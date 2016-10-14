package br.com.minegames.arqueiro.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.logging.Logger;

public class ExplodeListener implements Listener {

	private GameController game;

	public ExplodeListener(GameController plugin) {
		super();
		this.game = plugin;
	}

	@EventHandler
	public void onInteract(EntityInteractEvent event) {
		Location loc = event.getBlock().getLocation();
		Object aList[] = game.getLivePlayers().toArray();
		for (int i = 0; i < aList.length; i++) {
			Archer a = (Archer) aList[i];
			if (loc.getX() >= a.getSpawnPoint().getPointA().getBlockX()
					&& loc.getX() <= a.getSpawnPoint().getPointB().getBlockX()) {
				Logger.log("onIteract archer: " + a.getPlayer().getName() );
				game.damageArcherArea(a, event.getEntity());
			}
		}
	}

}
