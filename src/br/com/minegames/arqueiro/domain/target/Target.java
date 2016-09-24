package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;

public class Target {

	protected boolean hit;
	protected Player shooter;
	protected GameController game;
	protected int hitPoints = 10;
	
	public Target(GameController game) {
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
