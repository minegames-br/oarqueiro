package br.com.minegames.arqueiro.domain;

import org.bukkit.entity.Skeleton;

import br.com.minegames.arqueiro.Game;

public class SkeletonTarget extends EntityTarget {
	
	protected Skeleton skeleton;
	
	public SkeletonTarget(Game game, Skeleton skeleton) {
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
