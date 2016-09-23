package br.com.minegames.arqueiro.domain;

import org.bukkit.entity.Zombie;

import br.com.minegames.arqueiro.Game;

public class ZombieTarget extends EntityTarget {
	
	protected Zombie zombie;
	
	public Zombie getZombie() {
		return zombie;
	}

	public void setZombie(Zombie zombie) {
		this.zombie = zombie;
	}

	public ZombieTarget(Game game, Zombie zombie) {
		super(game, zombie);
		this.zombie = zombie;
		this.hitPoints = 50;
	}
	
	@Override
	public void destroy() {
		zombie.damage(1000);
	}
}
