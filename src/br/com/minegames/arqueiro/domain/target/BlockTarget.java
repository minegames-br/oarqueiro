package br.com.minegames.arqueiro.domain.target;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;

public class BlockTarget extends Target {
	
	protected Block block;
	protected Long creationTime;
	private int weigth;
	
	public BlockTarget(Block block) {
		super();
		this.block = block;
		this.hitPoints = 50;
		this.creationTime = System.currentTimeMillis();
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public Block getBlock() {
		return this.block;
	}
	
	public void hitTarget2(Player player) {
		super.hitTarget2(player);
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}
	
	public Long lifeTime() {
		return System.currentTimeMillis() - this.creationTime;
	}

	public int getWeigth() {
		return weigth;
	}

	public void setWeigth(int weigth) {
		this.weigth = weigth;
	}
	

}
