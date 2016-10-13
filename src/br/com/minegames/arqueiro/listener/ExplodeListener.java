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

		Logger.log(("onInteract" + event.getEntity() + " " + loc.getX()));

		Object aList[] = game.getLivePlayers().toArray();

		for (int i = 0; i < aList.length; i++) {

			Archer a = (Archer) aList[i];

			if (i == 0) {
				if (loc.getX() >= game.getPlayer1Arena().getPointA().getBlockX()
						&& loc.getX() <= game.getPlayer1Arena().getPointB().getBlockX()) {
					game.damageArcherArea(event.getEntity());
				}
			}
			if (i == 1) {
				if (loc.getX() >= game.getPlayer2Arena().getPointA().getBlockX()
						&& loc.getX() <= game.getPlayer2Arena().getPointB().getBlockX()) {
					game.damageArcherArea(event.getEntity());
				}
			}
			if (i == 2) {
				if (loc.getX() >= game.getPlayer3Arena().getPointA().getBlockX()
						&& loc.getX() <= game.getPlayer3Arena().getPointB().getBlockX()) {
					game.damageArcherArea(event.getEntity());
				}
			}
			if (i == 3) {
				if (loc.getX() >= game.getPlayer4Arena().getPointA().getBlockX()
						&& loc.getX() <= game.getPlayer4Arena().getPointB().getBlockX()) {
					game.damageArcherArea(event.getEntity());
				}
			}
		}
	}

}
