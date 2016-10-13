package br.com.minegames.arqueiro.task;

import java.util.Iterator;

import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;

public class ExplodeZombieTask extends BukkitRunnable {

	private GameController controller;

	public ExplodeZombieTask(GameController game) {
		this.controller = game;
	}

	@Override
	public void run() {
		// Explodir o zombie caso ele esteja na área de algum jogador
		// A explosão irá causar dano aos blocks de cerca perto do zombie
		Iterator<EntityTarget> iterator = this.controller.getLivingTargets().iterator();
		while (iterator.hasNext()) {
			EntityTarget t = iterator.next();
			if (t instanceof ZombieTarget) {
				EntityTarget e = (EntityTarget) t;
				Zombie entity = (Zombie) e.getLivingEntity();
				if (controller.shouldExplodeZombie(entity.getLocation())) {
					// Bukkit.broadcastMessage("Zombie is in region");
					//controller.explodeZombie(entity);
				}
			}
		}

	}

}
