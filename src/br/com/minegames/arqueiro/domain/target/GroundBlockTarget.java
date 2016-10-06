package br.com.minegames.arqueiro.domain.target;

import org.bukkit.block.Block;

public class GroundBlockTarget extends BlockTarget {
	
	public GroundBlockTarget(Block block) {
		super(block);
		this.hitPoints = 100;
		this.setWeigth(5);
	}

}
