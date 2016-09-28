package br.com.minegames.arqueiro.domain.target;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;

public class EntityTarget extends Target {

	protected int killPoints = 100;
	protected LivingEntity entity;
	protected Player killer;

	public EntityTarget(LivingEntity entity) {
		super();
		this.entity = entity;
	}
	
	public int getKillPoints() {
		return this.killPoints;
	}

	public void kill(Player player) {
		Location loc = entity.getLocation();
	}
	
	public LivingEntity getLivingEntity() {
		return this.entity;
	}

	public Player getKiller() {
		return killer;
	}

	public void setKiller(Player killer) {
		this.killer = killer;
	}

	
}
