package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Zombie;

public class ZombieTarget extends EntityTarget {
	
	protected Zombie zombie;

	public ZombieTarget(Zombie zombie) {
		super(zombie);
		this.zombie = zombie;
		this.setKillPoints(15);
	}
	
	public Zombie getZombie() {
		return zombie;
	}

	public void setZombie(Zombie zombie) {
		this.zombie = zombie;
	}
	
}
