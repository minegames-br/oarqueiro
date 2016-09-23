package br.com.minegames.arqueiro.domain;

import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.Game;

public class Target {

	protected boolean hit;
	protected Player shooter;
	protected Game game;
	protected int hitPoints = 10;
	
	public Target(Game game) {
		this.game = game;
	}
	
	public boolean isHit() {
		return this.hit;
	}
	
	public void hitTarget2(Player shooter) {
		this.hit = true;
		this.shooter = shooter;
		
		game.givePoints(shooter, this.getHitPoints());
	}
	
	public Player getShooter() {
		return this.shooter;
	}
	
	public int getHitPoints() {
		return hitPoints;
	}

	public void destroy() {
	}
	
}
