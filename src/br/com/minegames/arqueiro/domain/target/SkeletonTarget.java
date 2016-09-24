package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Skeleton;

import br.com.minegames.arqueiro.GameController;

public class SkeletonTarget extends EntityTarget {
	
	protected Skeleton skeleton;
	
	public SkeletonTarget(GameController game, Skeleton skeleton) {
		super(game, skeleton);
		this.skeleton = skeleton;
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}	

}
