package br.com.minegames.arqueiro.domain;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.Game;

public class EntityTarget extends Target {

	protected int killPoints = 100;
	protected LivingEntity entity;
	protected Player killer;

	public EntityTarget(Game game, LivingEntity entity) {
		super(game);
		this.entity = entity;
	}
	
	public int getKillPoints() {
		return this.killPoints;
	}

	public void kill(Player player) {
		Location loc = entity.getLocation();
	    game.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ()-1, 1.0F, false, false);
	    game.givePoints(player, this.getKillPoints());
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
