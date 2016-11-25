package br.com.minegames.arqueiro.domain;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.domain.GamePlayer;

public class Archer extends GamePlayer implements Comparable {

	public static int HIT_TARGET = 50;
	public static int KILL_SKELETON = 200;
	public static int KILL_BAT = 100;
	public static int HIT_SLOW_MOVING_TARGET = 100;
	public static int HIT_FAST_MOVING_TARGET = 200;

	private double baseHealth = 1;
	private ArcherBow bow;
	
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
	
	
}
