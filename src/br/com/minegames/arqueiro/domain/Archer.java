package br.com.minegames.arqueiro.domain;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Archer implements Comparable{

	public static int HIT_TARGET = 50;
	public static int KILL_SEKELETON = 200;
	public static int KILL_BAT = 100;
	public static int HIT_SLOW_MOVING_TARGET = 100;
	public static int HIT_FAST_MOVING_TARGET = 200;

	private Player player;
	private Integer point = 0;
	private double baseHealth = 1;
	private Area2D spawnPoint;
	private ArcherBow bow;
	private BossBar baseBar;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
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
		this.baseHealth = this.baseHealth - 0.1;
	}
	
	public void setBaseHealth(double value) {
		this.baseHealth = value;
	}
	
	public double getBaseHealth() {
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
	
	@Override
	public int compareTo(Object o) {
		Archer archer = (Archer) o;
		return this.getPoint().compareTo(archer.getPoint());
	}
	public void setBow(ArcherBow d) {
		this.bow = d;
	}
	public Object getBow() {
		return this.bow;
	}
	
	public void addBaseBar(BossBar bar) {
		this.baseBar = bar;
	}
	
	public BossBar getBaseBar() {
		return this.baseBar;
	}
	
}
