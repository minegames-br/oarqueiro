package br.com.minegames.arqueiro.domain;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.Game;

public class BlockTarget extends Target {
	
	protected Block block;
	
	public BlockTarget(Game game, Block block) {
		super(game);
		this.block = block;
		this.hitPoints = 50;
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
	

}
