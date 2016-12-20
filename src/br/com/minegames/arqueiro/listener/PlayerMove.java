package br.com.minegames.arqueiro.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.minigame.domain.GamePlayer;
import com.thecraftcloud.minigame.service.ConfigService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import net.md_5.bungee.api.ChatColor;

public class PlayerMove implements Listener {

	private ConfigService configService = ConfigService.getInstance();

	public PlayerMove(GameController plugin) {
		super();
		this.controller = plugin;
	}

	private GameController controller;

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		if (configService.getMyCloudCraftGame().isStarted()) {
			for (GamePlayer gp : controller.getLivePlayers()) {
				Archer archer = (Archer) gp;
				Player player = event.getPlayer();

				int x1 = archer.getArea().getPointA().getX();
				int z1 = archer.getArea().getPointA().getZ();

				int x2 = archer.getArea().getPointB().getX();
				int z2 = archer.getArea().getPointB().getZ();

				Location pLoc = player.getLocation();

				if (this.configService.getArena().getFacing() == FacingDirection.NORTH) {
					if (pLoc.getX() > x1+2 || pLoc.getX() < x2-1 || pLoc.getZ() < z1 || pLoc.getZ() > z2+2) {
						player.sendMessage(ChatColor.RED + "Você não pode sair da sua área");
						Location spawnPoint = new Location(player.getWorld(), archer.getSpawnPoint().getX(),
								archer.getSpawnPoint().getY(), archer.getSpawnPoint().getZ(), 180, 0);
						player.teleport(spawnPoint);
					}

				} else if (this.configService.getArena().getFacing() == FacingDirection.EAST) {
					if (pLoc.getX() > x1+1 || pLoc.getX() < x2 || pLoc.getZ() > z1+1 || pLoc.getZ() < z2-1) {
						player.sendMessage(ChatColor.RED + "Você não pode sair da sua área");
						Location spawnPoint = new Location(player.getWorld(), archer.getSpawnPoint().getX(),
								archer.getSpawnPoint().getY(), archer.getSpawnPoint().getZ(), 270, 0);
						player.teleport(spawnPoint);
					}

				}

			}

		}

		return;

	}

}
