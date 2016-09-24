package br.com.minegames.arqueiro.task;

import java.util.Vector;

import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.Target;

public class DestroyTargetTask extends BukkitRunnable {
	
	private GameController game;
	
	public DestroyTargetTask(GameController game) {
		this.game = game;
	}
	
    @Override
    public void run() {
    	Vector <Target> targets = game.getTargets();
    	
    	for (Target target : targets) {
    		if (target instanceof BlockTarget) {
    			BlockTarget btarget = (BlockTarget) target;
    			if (btarget.lifeTime() > 10000) {
    				target.destroy();
    				targets.remove(target);
    			}
    		}
    	}
    }
    
}
