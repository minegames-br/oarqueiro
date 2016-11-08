package br.com.minegames.arqueiro.listener;

import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.domain.GamePlayer;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;

public class ExplodeListener implements Listener {

	private GameController controller;

	public ExplodeListener(GameController plugin) {
		super();
		this.controller = plugin;
	}

	@EventHandler
	public void onInteract(EntityInteractEvent event) {
		Location loc = event.getEntity().getLocation();
		Object aList[] = controller.getLivePlayers().toArray();

		CopyOnWriteArraySet<Area3D> playerSpawnList = (CopyOnWriteArraySet)controller.getGameArenaConfigByGroup("PLAYER-SPAWN");
		for(Area3D area3d: playerSpawnList) {
			for(GamePlayer gp: controller.getLivePlayers()) {
				Archer archer = (Archer)gp;
				if(isInsideArea(loc, area3d)) { 
					MGLogger.info("iria explodir");
					controller.killEntity(event.getEntity());
				}
			}
		}
		
	}
	
	private boolean isInsideArea(Location loc, Area3D area) {
		boolean result = false;
		
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		
		int xa = area.getPointA().getX();
		int xb = area.getPointB().getX();
		
		int za = area.getPointA().getZ();
		int zb = area.getPointB().getZ();
		
		if( ( (x >= xa && x <= xb) || (z >= zb && z <= za) ) 
				|| ( (x >= xb && x <= xa) ||  (z >= za && z <= zb) ) ) {
			result = true;
		}
		
		return result;
	}

}