package br.com.minegames.arqueiro;

import br.com.minegames.core.GameConfig;
import br.com.minegames.core.domain.Area3D;
import br.com.minegames.core.domain.Local;

public class ArqueiroConfig extends GameConfig {
	/*
	protected int maxZombieSpawned = 5;
	protected int maxTarget = 3;
	protected int maxMovingTarget = 3;
	


	public int getMaxZombieSpawned() {
		Integer value = (Integer)variables.get(ArqueiroConfig.MAX_ZOMBIE_SPAWNED_PER_PLAYER);
		if( value != null) {
			return value;
		} else {
			return 5;
		}
	}

	public void setMaxZombieSpawned(int value) {
		this.addVariable(MAX_ZOMBIE_SPAWNED_PER_PLAYER, value);
	}


	public int getMaxTarget() {
		Integer value = (Integer)variables.get(ArqueiroConfig.MAX_TARGET);
		if( value != null) {
			return value;
		} else {
			return 3;
		}
	}

	public void setMaxTarget(int value) {
		this.addVariable(MAX_TARGET, value);
	}

	public int getMaxMovingTarget() {
		Integer value = (Integer)variables.get(ArqueiroConfig.MAX_MOVING_TARGET);
		if( value != null) {
			return value;
		} else {
			return 3;
		}
	}

	public void setMaxMovingTarget(int value) {
		this.addVariable(MAX_MOVING_TARGET, value);
	}

	public void init() {
		this.setCountDown(10);
		
		Local a1 = new Local(457, 4, 1165);
		Local a2 = new Local(493, 18, 1200);
		this.gameArena = new Area3D(a1, a2);
		
		Local f1 = new Local(459, 10, 1170);
		Local f2 = new Local(491, 14, 1197);
		Area3D floatingArea = new Area3D(f1, f2);
		this.addVariable(FLOATING_AREA, floatingArea);

		Local b1 = new Local(457, 6, 1200);
		Local b2 = new Local(493, 18, 1200);
		Area3D blackWall = new Area3D(b1, b2);
		this.addVariable(BLACK_WALL, blackWall);

		Local s1 = new Local(460, 6, 1180);
		Local s2 = new Local(490, 6, 1200);
		Area3D monstersSpawnArea = new Area3D(s1, s2);
		this.addVariable(MONSTERS_SPAWN_AREA, monstersSpawnArea);

		Local pointA = null;
		Local pointB = null;
		Area3D spawnArea = null;
				
		int y = 4;
		int z = 1164;
		int zEnd = 1169;
		
		pointA = new Local(459, y, z);
		pointB = new Local(466, y, zEnd);
		spawnArea = new Area3D(pointA, pointB);
		this.addPlayerSpawnArea(spawnArea);
		
		pointA = new Local(468, y, z);
		pointB = new Local(475, y, zEnd);
		spawnArea = new Area3D(pointA, pointB);
		this.addPlayerSpawnArea(spawnArea);
		
		pointA = new Local(477, y, z);
		pointB = new Local(484, y, zEnd);
		spawnArea = new Area3D(pointA, pointB);
		this.addPlayerSpawnArea(spawnArea);
		
		pointA = new Local(486, y, z);
		pointB = new Local(493, y, zEnd);
		spawnArea = new Area3D(pointA, pointB);
		this.addPlayerSpawnArea(spawnArea);

		Local lobbyLocation = new Local(530, 4, 1210);
		this.addVariable( this.LOBBY_LOCATION, lobbyLocation );
		
		Local firstPositionLocation = new Local(551, 19, 1227);
		this.addVariable( "firstPositionLocation", firstPositionLocation );
		Local secondPositionLocation = new Local(557, 14, 1220);
		this.addVariable( "secondPositionLocation", secondPositionLocation );
		Local thirdPositionLocation = new Local(564, 10, 1227);
		this.addVariable( "thirdPositionLocation", thirdPositionLocation );
		Local fourthPositionLocation = new Local(557, 5, 1227);
		this.addVariable( "fourthPositionLocation", fourthPositionLocation );


	}

	public Object getVariable(String key) {
		return this.variables.get(key);
	}
	*/
}
