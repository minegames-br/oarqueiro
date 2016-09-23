package br.com.minegames.arqueiro.domain;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.util.BlockManipulationUtil;

public class WallBlockTarget extends BlockTarget {
	
	public WallBlockTarget(Game game, Block block) {
		super(game, block);
	}

	@Override
	public void hitTarget2(Player player) {
		// TODO Auto-generated method stub
		super.hitTarget2(player);
		
		Location loc = block.getLocation();
	    game.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ()-1, 2.0F, false, false);

	    this.destroy();
	}
	
	@Override
	public void destroy() {
	    //restaurar a parte preta
	    Location l1 = new Location(game.getWorld(), block.getX()-1, block.getY()-1, block.getZ());
	    Location l2 = new Location(game.getWorld(), block.getX()+1, block.getY()+1, block.getZ());
	    BlockManipulationUtil.createWoolBlocks(l1, l2, DyeColor.BLACK);
	}

	
	
	
}
