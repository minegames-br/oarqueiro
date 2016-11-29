package br.com.minegames.arqueiro.service;

import org.bukkit.Location;
import org.bukkit.World;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.util.LocationUtil;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;

public class LocalService {
	private GameController controller;
	private LocationUtil locationUtil = new LocationUtil();
	private ConfigService configService = ConfigService.getInstance();

	public LocalService(GameController controller) {
		this.controller = controller;
	}
	
	public Location getRandomSpawnLocationForGroundEnemy() {
		Area3D area = (Area3D)configService.getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		return locationUtil.getRandomLocationXYZ( configService.getWorld(), area);
	}

	public Location getRandomSpawnLocationForVerticalMovingTarget() {
		Area3D area = (Area3D)configService.getGameArenaConfig(Constants.FLOATING_AREA);
		return locationUtil.getRandomLocationXYZ( configService.getWorld(), area);
	}

	public Location getRandomSpawnLocationForGroundTarget() {
		Area3D area = (Area3D)configService.getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		return locationUtil.getRandomLocationXYZ( configService.getWorld() , area);
	}

	public Location getRandomSpawnLocationForFloatingTarget() {
		Area3D area = (Area3D)configService.getGameArenaConfig(Constants.FLOATING_AREA);
		return locationUtil.getRandomLocationXYZ( configService.getWorld(), area);
	}

	public Location getMiddle(World world, Area3D area3d) {
		return locationUtil.getMiddle(world, area3d);
	}
}
