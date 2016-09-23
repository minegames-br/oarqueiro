package br.com.minegames.arqueiro.domain;

import org.bukkit.Location;

public class Area3D {
	private Location pointA;
	private Location pointB;

	public Area3D() {
		
	}
	
	public Area3D(Location pointA, Location pointB) {
		this.pointA = pointA;
		this.pointB = pointB;
	}
	
	public Location getPointA() {
		return pointA;
	}
	public void setPointA(Location pointA) {
		this.pointA = pointA;
	}
	public Location getPointB() {
		return pointB;
	}
	public void setPointB(Location pointB) {
		this.pointB = pointB;
	}

	public int getXSize() {
		return this.pointB.getBlockX()-this.pointA.getBlockX();
	}
	
	public int getYSize() {
		return this.pointB.getBlockY()-this.pointA.getBlockY();
	}
	
	public int getZSize() {
		return this.pointB.getBlockZ()-this.pointA.getBlockZ();
	}
	
}
