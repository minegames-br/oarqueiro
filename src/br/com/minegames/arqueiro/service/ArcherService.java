package br.com.minegames.arqueiro.service;

import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.core.util.title.TitleUtil;
import com.thecraftcloud.minigame.domain.GamePlayer;
import com.thecraftcloud.minigame.service.PlayerService;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.ArcherBow;

public class ArcherService extends PlayerService {

	private GameController controller;

	public ArcherService(GameController controller) {
		super(controller);
		this.controller = controller;
	}

	public void shootArrows(Player player) {
		int iArrows = 1;
		Archer archer = (Archer) this.findGamePlayerByPlayer(player);
		if (archer.getBow().equals(ArcherBow.DEFAULT)) {
			return;
		} else if (archer.getBow().equals(ArcherBow.DOUBLE)) {
			iArrows = 1;
		} else if (archer.getBow().equals(ArcherBow.TRIPPLE)) {
			iArrows = 2;
		}

		Location player_location = player.getLocation();
		for (int i = 0; i <= iArrows; i++) {

			if (i == 1 && iArrows == 2) {
				continue;
			} else if (i == 1 && iArrows == 1) {
				break;
			}
			int spread = 0;

			if (i == 0)
				spread = -2;
			else if (i == 2)
				spread = 2;

			double pitch = ((player_location.getPitch() + 90) * Math.PI) / 180;
			double yaw = ((player_location.getYaw() + 90 + spread) * Math.PI) / 180;

			double z_axis = Math.sin(pitch);
			double x = z_axis * Math.cos(yaw);
			double y = z_axis * Math.sin(yaw);
			double z = Math.cos(pitch);

			Vector vector = new Vector(x, z, y);
			vector.multiply(3);

			player.launchProjectile(Arrow.class, vector);
		}
	}

	public boolean damageArcherArea(Archer archer) {
		if (archer != null) {
			if (archer.getBaseHealth() < 0) {
				archer.getBaseBar().setProgress(0);
				return false;
			} else {
				archer.damageBase();
				if (archer.getBaseHealth() >= 0) {
					archer.getBaseBar().setProgress(new Double(archer.getBaseHealth()));
				}
			}
		}
		return true;
	}

	public void destroyBase(Archer archer, int localX, int localZ) {

		if (this.configService.getArena().getFacing() == FacingDirection.EAST
				|| this.configService.getArena().getFacing() == FacingDirection.WEST) {
			Location l = new Location(configService.getWorld(), archer.getArea().getPointA().getX(),
					archer.getArea().getPointA().getY(), localZ);
			configService.getWorld().getBlockAt(l).setType(Material.AIR);

		} else if (this.configService.getArena().getFacing() == FacingDirection.NORTH
				|| this.configService.getArena().getFacing() == FacingDirection.SOUTH) {
			Location l = new Location(configService.getWorld(), localX, archer.getArea().getPointA().getY(),
					archer.getArea().getPointA().getZ());
			configService.getWorld().getBlockAt(l).setType(Material.AIR);
		}

	}

	public void giveBonus(Player shooter) {
		Archer archer = (Archer) this.findGamePlayerByPlayer(shooter);
		if (archer.getBow().equals(ArcherBow.DEFAULT)) {
			Bukkit.getConsoleSender().sendMessage(Utils.color("&6[DOUBLE ARROW]"));
			archer.setBow(ArcherBow.DOUBLE);
		} else if (archer.getBow().equals(ArcherBow.DOUBLE)) {
			Bukkit.getConsoleSender().sendMessage(Utils.color("&6[TRIPPLE ARROW]"));
			archer.setBow(ArcherBow.TRIPPLE);
		}
	}

	public void killPlayer(Player dead) {
		String deadname = dead.getDisplayName();
		Bukkit.broadcastMessage(ChatColor.GOLD + " " + deadname + "" + ChatColor.GREEN + " died.");

		dead.setHealth(20); // Do not show the respawn screen
		dead.getInventory().clear();

		if (this.configService.getMyCloudCraftGame().isStarted()) {
			this.controller.removeLivePlayer(dead);
		}
	}

	public void givePointsByTarget(Player player, Location location) {
		Archer archer = (Archer) this.findGamePlayerByPlayer(player);
		Integer bonus = null;
		int points = 30;

		int xP = Math.abs(player.getLocation().getBlockX() - location.getBlockX());
		int yP = Math.abs(player.getLocation().getBlockY() - location.getBlockY());
		int zP = Math.abs(player.getLocation().getBlockZ() - location.getBlockZ());
		bonus = xP + yP + zP;

		int totalPoints = (int) (points + bonus * 1.5);
		archer.addPoints(totalPoints);
		player.sendMessage(ChatColor.GOLD + "" + points + " Pontos + " + bonus + " de B�nus!");
		updateScoreBoards();
	}

	public void givePoints(Player player, Integer hitpoints) {
		Archer archer = (Archer) this.findGamePlayerByPlayer(player);
		archer.addPoints(hitpoints);
		updateScoreBoards();
	}

	public void teleportPlayersToPodium() {
		Object aList[] = this.controller.getLivePlayers().toArray();
		Arrays.sort(aList);
		// MGLogger.trace("teleport players to podium - aList.lengh: " +
		// aList.length + "");
	}

	/*
	 * Nesse m�todo poderemos decidir o que dar a cada jogador
	 */
	public void setupPlayerToStartGame(Player player) {
		PlayerInventory inventory = player.getInventory();

		inventory.clear();
		inventory.setArmorContents(null);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 15);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 15);

		inventory.setItemInMainHand(bow);
		inventory.addItem(arrow);
		inventory.addItem(sword);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective(Utils.color("&6Placar"), "placar");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
	}

	public void updateScoreBoards() {
		for (GamePlayer gp : this.controller.getLivePlayers()) {
			Archer archer = (Archer) gp;
			Player player = archer.getPlayer();
			Scoreboard scoreboard = player.getScoreboard();
			for (GamePlayer gp1 : this.controller.getLivePlayers()) {
				Archer a1 = (Archer) gp1;
				String name = a1.getPlayer().getName();
				scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(name).setScore(a1.getPoint());
			}
		}
	}

	public BossBar createBossBar() {
		BossBar bar = Bukkit.createBossBar("Base", BarColor.PINK, BarStyle.SOLID);
		bar.setProgress(1F);
		return bar;
	}

	public Archer findArcherByPlayer(Player player) {

		if (player != null) {
			for (GamePlayer gp : controller.getLivePlayers()) {
				if (gp.getPlayer().equals(player)) {
					Archer archer = (Archer) gp;
					return archer;
				}

			}
		}

		return null;

	}
}
