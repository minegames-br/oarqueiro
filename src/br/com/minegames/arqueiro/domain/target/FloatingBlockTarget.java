package br.com.minegames.arqueiro.domain.target;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FloatingBlockTarget extends BlockTarget {
	
	public FloatingBlockTarget(Block block) {
		super(block);
		this.hitPoints = 150;
		this.setWeigth(7);
	}

	@Override
	public void hitTarget2(Player player) {
		// TODO Auto-generated method stub
		super.hitTarget2(player);
		
		Location loc = block.getLocation();
	}

}
