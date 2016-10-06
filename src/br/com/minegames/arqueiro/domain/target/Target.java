package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Player;

public class Target {

	protected boolean hit;
	protected Player shooter;
	protected int hitPoints = 10;
	
	public Target() {
	}
	
	public boolean isHit() {
		return this.hit;
	}
	
	public void hitTarget2(Player shooter) {
		this.hit = true;
		this.shooter = shooter;
	}
	
	public Player getShooter() {
		return this.shooter;
	}
	
	public int getHitPoints() {
		return hitPoints;
	}


}
