package br.com.minegames.arqueiro.domain.target;

import org.bukkit.entity.Skeleton;

public class SkeletonTarget extends EntityTarget {
	
	protected Skeleton skeleton;
	
	public SkeletonTarget(Skeleton skeleton) {
		super(skeleton);
		this.skeleton = skeleton;
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}	

}
