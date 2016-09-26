package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Zombie;

import br.com.minegames.arqueiro.GameController;

public class ZombieTarget extends EntityTarget {
	
	protected Zombie zombie;
	
	public Zombie getZombie() {
		return zombie;
	}

	public void setZombie(Zombie zombie) {
		this.zombie = zombie;
	}

	public ZombieTarget(GameController game, Zombie zombie) {
		super(game, zombie);
		this.zombie = zombie;
		this.hitPoints = 50;
	}
	
	@Override
	public void destroy() {
		zombie.damage(1000);
	}
}