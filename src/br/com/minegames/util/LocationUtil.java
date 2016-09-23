package br.com.minegames.util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import br.com.minegames.arqueiro.domain.Area2D;
import br.com.minegames.arqueiro.domain.Area3D;
import br.com.minegames.logging.Logger;

public class LocationUtil {
	
	public static Location getRandomLocationXZ( World _world, Area2D area ) {

		if(area.getYSize() != 0) {
			Logger.log("Não é uma Area2D - Y");
		}
		
		Location pointA = area.getPointA();
		Location pointB = area.getPointB();
		
		// Como são entidades que não podem voar, definir o spawn no chão
		// a area 2D deve ter Y igual para os pontos A e B 
		int y =  pointA.getBlockY();

		int xSize = pointB.getBlockX() - pointA.getBlockX();
		int zSize = pointB.getBlockZ() - pointA.getBlockZ();
		
		int x = new Random().nextInt(xSize);
		int z = new Random().nextInt(zSize);
		
		z = z + area.getPointA().getBlockZ();
		x = x + area.getPointA().getBlockX();
		
		Location spawnLocation = new Location(_world, x, y, z);
		return spawnLocation;
		
	}
	
	public static Location getRandomLocationXY( World _world, Area2D area ) {

		if(area.getZSize() != 0) {
			Logger.log("Não é uma Area2D - Z");
		}
		
		Location pointA = area.getPointA();
		Location pointB = area.getPointB();
		
		int ySize =  pointB.getBlockY() - pointA.getBlockY();
		int xSize = pointB.getBlockX() - pointA.getBlockX();
		
		//Z não se altera (provavel parede)
		int z = pointB.getBlockZ();
		
		int x = new Random().nextInt(xSize);
		int y = new Random().nextInt(ySize);
		
		y = y + area.getPointA().getBlockY();
		x = x + area.getPointA().getBlockX();
		
		Location spawnLocation = new Location(_world, x, y, z);
		return spawnLocation;
	}
	
	public static Location getRandomLocationXYZ( World _world, Area3D area ) {
		Location pointA = area.getPointA();
		Location pointB = area.getPointB();
		
		int xSize = pointB.getBlockX() - pointA.getBlockX();
		int ySize = pointB.getBlockY() - pointA.getBlockY();
		int zSize = pointB.getBlockZ() - pointA.getBlockZ();
		
		//Z não se altera (provavel parede)
		int x = new Random().nextInt(xSize);
		int y = new Random().nextInt(ySize);
		int z = new Random().nextInt(zSize);
		
		x = x + area.getPointA().getBlockX();
		y = y + area.getPointA().getBlockY();
		z = z + area.getPointA().getBlockZ();
		
		Location spawnLocation = new Location(_world, x, y, z);
		return spawnLocation;
	}
	
}
