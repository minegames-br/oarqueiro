package br.com.minegames.arqueiro.task;

import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.service.TargetService;

public class DestroyTargetTask extends BukkitRunnable {
	
	private GameController controller;
	private TargetService targetService;
	
	public DestroyTargetTask(GameController controller) {
		this.controller = controller;
		this.targetService = new TargetService(controller);
	}
	
    @Override
    public void run() {
    	CopyOnWriteArraySet <Target> targets = controller.getTargets();
    	
    	for (Target target : targets) {
    		if (target instanceof BlockTarget) {
    			BlockTarget btarget = (BlockTarget) target;
    			if (btarget.lifeTime() > 10000) {
    				targetService.destroyBlockTarget(btarget);
    				targets.remove(target);
    			}
    		}
    	}
    }
    
}
