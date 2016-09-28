package br.com.minegames.arqueiro.domain.target;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MovingTarget extends BlockTarget {
	
	protected Material oldBlockType;
	
	public MovingTarget(Block block) {
		super(block);
	}
	
	public void setOldBlockType(Material m) {
		this.oldBlockType = m;
	}
	
	public Material getOldBlockType() {
		return this.oldBlockType;
	}

	@Override
	public void hitTarget2(Player shooter) {
		this.hit = true;
		this.shooter = shooter;
	}

}
