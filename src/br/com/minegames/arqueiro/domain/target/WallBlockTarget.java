package br.com.minegames.arqueiro.domain.target;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WallBlockTarget extends BlockTarget {
	
	public WallBlockTarget(Block block) {
		super(block);
		this.hitPoints = 200;
		this.setWeigth(10);
	}

	@Override
	public void hitTarget2(Player player) {
		// TODO Auto-generated method stub
		super.hitTarget2(player);
	}
	
	
	
}
