package br.com.minegames.arqueiro.domain.target;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.util.BlockManipulationUtil;

public class FloatingBlockTarget extends BlockTarget {
	
	public FloatingBlockTarget(Block block) {
		super(block);
		this.hitPoints = 50;
	}

	@Override
	public void hitTarget2(Player player) {
		// TODO Auto-generated method stub
		super.hitTarget2(player);
		
		Location loc = block.getLocation();
	}

}
