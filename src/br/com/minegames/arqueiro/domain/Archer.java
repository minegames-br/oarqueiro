package br.com.minegames.arqueiro.domain;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Archer {

	public static int HIT_TARGET = 50;
	public static int KILL_SEKELETON = 200;
	public static int KILL_BAT = 100;
	public static int HIT_SLOW_MOVING_TARGET = 100;
	public static int HIT_FAST_MOVING_TARGET = 200;

	private Player player;
	private int point;
	private int baseHealth = 5;
	private Area2D spawnPoint;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	
	public void addPoints( int value ) {
		this.point = this.point + value;
	}
	
	public void removePoints( int value ) {
		this.point = this.point - value;
		if( this.point < 0) {
			this.point = 0;
		}
	}
	
	public int getCurrentArrowDamage() {
		return 30;
	}
	
	public void damageBase() {
		this.baseHealth = this.baseHealth - 1;
	}
	
	public int getBaseHealth() {
		return this.baseHealth;
	}
	
	public void setSpawnPoint(Area2D l) {
		this.spawnPoint = l;
	}
	
	public Area2D getSpawnPoint() {
		return this.spawnPoint;
	}
	public boolean isNear(Location l) {
		int x = l.getBlockX();
		int minX = spawnPoint.getPointA().getBlockX();
		int maxX = spawnPoint.getPointA().getBlockX();
		int maxZ = spawnPoint.getPointB().getBlockZ();
		if( x >= minX || x <= maxX) {
			if(l.getBlockZ() == maxZ+1) {
				return true;
			}
		}
		return false;
	}
	
}
