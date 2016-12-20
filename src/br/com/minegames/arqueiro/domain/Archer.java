package br.com.minegames.arqueiro.domain;

import org.bukkit.entity.Player;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.minigame.domain.GamePlayer;

public class Archer extends GamePlayer {

	public static int HIT_TARGET = 50;
	public static int KILL_SKELETON = 200;
	public static int KILL_BAT = 100;
	public static int HIT_SLOW_MOVING_TARGET = 100;
	public static int HIT_FAST_MOVING_TARGET = 200;

	private double baseHealth = 1;
	private ArcherBow bow;
	private Area3D area;
	
	public void damageBase() {
		if(this.baseHealth > 0) {
			this.baseHealth = (this.baseHealth - 0.1d);
		}
	}
	
	public double getBaseHealth() {
		return this.baseHealth;
	}
	
	public void regainHealthToPlayer(Archer archer) {
        Player player = archer.getPlayer();
        player.setHealth(player.getMaxHealth());
    }
	
	public void setBow(ArcherBow d) {
		this.bow = d;
	}
	public Object getBow() {
		return this.bow;
	}

	public Area3D getArea() {
		return area;
	}

	public void setArea(Area3D area) {
		this.area = area;
	}

	
	
}
