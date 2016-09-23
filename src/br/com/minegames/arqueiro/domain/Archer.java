package br.com.minegames.arqueiro.domain;

import org.bukkit.entity.Player;

public class Archer {

	public static int HIT_TARGET = 50;
	public static int KILL_SEKELETON = 200;
	public static int KILL_BAT = 100;
	public static int HIT_SLOW_MOVING_TARGET = 100;
	public static int HIT_FAST_MOVING_TARGET = 200;

	private Player player;
	private int point;
	
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
	
}
