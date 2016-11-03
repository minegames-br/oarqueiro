package br.com.minegames.arqueiro.domain.target;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MovingTarget extends BlockTarget {
	
	protected Material oldBlockType;
	protected Integer maxMoves;
	protected Integer moves;
	
	public MovingTarget(Block block) {
		super(block);
	}
	
	public void setOldBlockType(Material m) {
		this.oldBlockType = m;
	}
	
	public Material getOldBlockType() {
		return this.oldBlockType;
	}

	public Integer getMaxMoves() {
		return maxMoves;
	}

	public void setMaxMoves(Integer maxMoves) {
		this.maxMoves = maxMoves;
	}

	@Override
	public void hitTarget2(Player shooter) {
		this.hit = true;
		this.shooter = shooter;
	}

	public Integer getMoves() {
		return moves;
	}

	public void setMoves(Integer moves) {
		this.moves = moves;
	}

}
