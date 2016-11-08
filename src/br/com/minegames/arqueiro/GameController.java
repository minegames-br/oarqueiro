package br.com.minegames.arqueiro;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.Arena;
import com.thecraftcloud.core.domain.Game;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.core.util.BlockManipulationUtil;
import com.thecraftcloud.core.util.LocationUtil;
import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.core.util.title.TitleUtil;
import com.thecraftcloud.domain.GamePlayer;
import com.thecraftcloud.plugin.MyCloudCraftPlugin;
import com.thecraftcloud.plugin.task.LevelUpTask;

import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.ArcherBow;
import br.com.minegames.arqueiro.domain.TheLastArcher;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;
import br.com.minegames.arqueiro.listener.BowShootListener;
import br.com.minegames.arqueiro.listener.EntityHitEvent;
import br.com.minegames.arqueiro.listener.ExplodeListener;
import br.com.minegames.arqueiro.listener.PlayerMove;
import br.com.minegames.arqueiro.listener.TargetHitEvent;
import br.com.minegames.arqueiro.task.DestroyTargetTask;
import br.com.minegames.arqueiro.task.ExplodeZombieTask;
import br.com.minegames.arqueiro.task.PlaceMovingTargetTask;
import br.com.minegames.arqueiro.task.PlaceTargetTask;
import br.com.minegames.arqueiro.task.SpawnSkeletonTask;
import br.com.minegames.arqueiro.task.SpawnZombieTask;

public class GameController extends MyCloudCraftPlugin {

	private Runnable placeTargetTask;
	private int placeTargetThreadID;
	private Runnable placeMovingTargetTask;
	private int placeMovingTargetThreadID;
	private Runnable destroyTargetTask;
	private int destroyTargetThreadID;
	private SpawnZombieTask spawnZombieTask;
	private int spawnZombieThreadID;
	private SpawnSkeletonTask spawnSkeletonTask;
	private int spawnSkeletonThreadID;
	private LevelUpTask levelUpTask;
	private int levelUpTaskID;

	private CopyOnWriteArraySet<Target> targets = new CopyOnWriteArraySet<Target>();
	private CopyOnWriteArraySet<EntityTarget> livingTargets = new CopyOnWriteArraySet<EntityTarget>();
	private CopyOnWriteArraySet<MovingTarget> movingTargets = new CopyOnWriteArraySet<MovingTarget>();

	private Runnable explodeZombieTask;
	private int explodeZombieThreadID;
	
	private LocationUtil locationUtil = new LocationUtil();

	private BlockManipulationUtil blockManipulationUtil = new BlockManipulationUtil();

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.setSpawnRadius(0);
		
		//ao criar, o jogo fica imediatamente esperando jogadores
		this.myCloudCraftGame = new TheLastArcher();

	}

	@Override
	public void init() {
		super.init();

		// inicializar variaveis de instancia
		this.placeTargetTask = new PlaceTargetTask(this);
		this.placeMovingTargetTask = new PlaceMovingTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.spawnSkeletonTask = new SpawnSkeletonTask(this);
		// this.spawnZombieTask = new SpawnZombieTask(this);
		this.explodeZombieTask = new ExplodeZombieTask(this);
		this.levelUpTask = new LevelUpTask(this);

	}

	@Override
	public void onDisable() {
		if (this.myCloudCraftGame.isStarted()) {
			this.myCloudCraftGame.shutDown();
			this.endGame();
		}
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerMove(this), this);
		pm.registerEvents(new TargetHitEvent(this), this);
		pm.registerEvents(new EntityHitEvent(this), this);
		pm.registerEvents(new BowShootListener(this), this);
		pm.registerEvents(new ExplodeListener(this), this);
	}

	/*
	 * Quando esse método rodar, já teremos a quantidade máxima de jogadores na
	 * arena ou então a quantidade mínima e o tempo de espera terminou.
	 */
	@Override
	public void startGameEngine() {
		super.startGameEngine();
		
		// registrar os Listeners de eventos do servidor e do jogo
		registerListeners();

		MGLogger.info("Game.startGameEngine");

		// preparar Score Board
		int loc = 1;
		for (GamePlayer gp : livePlayers) {
			Archer archer = (Archer)gp;
			Player player = archer.getPlayer();
			//this.world = player.getWorld();
			Area3D spawnPoint = (Area3D)getGameArenaConfig("arqueiro.player" + loc + ".area");
			archer.setSpawnPoint(spawnPoint);							   
			player.teleport(locationUtil.getMiddle(this.world, spawnPoint) );
			archer.regainHealthToPlayer(archer);

			// Preparar o jogador para a rodada. Dar armaduras, armas, etc...
			archer.setBow(ArcherBow.DEFAULT);

			BossBar bar = createBossBar();
			archer.addBaseBar(bar);
			bar.addPlayer(player);

			setupPlayerToStartGame(player);
			loc++;
		}

		updateScoreBoards();

		BukkitScheduler scheduler = getServer().getScheduler();

		// Iniciar threads do jogo
		this.placeTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.placeTargetTask, 0L, 50L);
		this.placeMovingTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.placeMovingTargetTask, 200L,
				15L);
		this.destroyTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.destroyTargetTask, 0L, 100L);
		this.spawnZombieThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnZombieTask, 0L, 50L);
		this.spawnSkeletonThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnSkeletonTask, 0L, 150L);
		this.levelUpTaskID = scheduler.scheduleSyncRepeatingTask(this, this.levelUpTask, 0L, 50L);

	}
	
	@Override
	public GamePlayer createGamePlayer() {
		Archer archer = new Archer();
		return archer;
	}

	public boolean shouldEndGame() {
    	//Terminar o jogo após o 10 Nível
    	if(this.myCloudCraftGame.getLevel().getLevel() >= 11 && this.myCloudCraftGame.isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - Time is Over"));
            return true;
    	}
    	
    	//Terminar o jogo caso não tenha mais jogadores
    	if( this.getLivePlayers().size() == 0  && this.myCloudCraftGame.isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - No more players"));
            return true;
    	}
    	return false;
	}

	private BossBar createBossBar() {
		BossBar bar = Bukkit.createBossBar("Base", BarColor.PINK, BarStyle.SOLID);
		bar.setProgress(1F);
		return bar;
	}

	private void updateScoreBoards() {
		for (GamePlayer gp : this.livePlayers) {
			Archer archer = (Archer)gp;
			Player player = archer.getPlayer();
			Scoreboard scoreboard = player.getScoreboard();
			for (GamePlayer gp1 : this.livePlayers) {
				Archer a1 = (Archer)gp1;
				String name = a1.getPlayer().getName();
				scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(name).setScore(a1.getPoint());
			}
		}
	}

	/*
	 * Nesse método poderemos decidir o que dar a cada jogador
	 */
	private void setupPlayerToStartGame(Player player) {
		PlayerInventory inventory = player.getInventory();

		inventory.clear();
		inventory.setArmorContents(null);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 15);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 15);

		inventory.addItem(bow);
		inventory.addItem(arrow);
		inventory.addItem(sword);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective(Utils.color("&6Placar"), "placar");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
	}

	/**
	 * Quando esse método executar, o jogo terá terminado com um vencedor e/ou o
	 * tempo terá acabado.
	 */
	@Override
	public void endGame() {
		super.endGame();
		if (this.myCloudCraftGame.isStarted()) {
			this.myCloudCraftGame.endGame();
		}
		MGLogger.info("Game.endGame");

		// Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.placeMovingTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnSkeletonThreadID);
		Bukkit.getScheduler().cancelTask(this.explodeZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.levelUpTaskID);

		// TODO o que vai acontecer com os jogadores quando acabar o jogo?
		// por enquanto vou tirá-los da arena e zerar os inventarios e recriar a
		// parede preta
		for (GamePlayer gp : livePlayers) {
			Archer archer = (Archer)gp;
			Player player = archer.getPlayer();
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			player.sendMessage("Você fez " + archer.getPoint() + " pontos.");
			archer.regainHealthToPlayer(archer);
		}

		// destroyTargets()
		destroyTargets();

		// restaurar parede preta
		//createBlackWall();

		// manda os jogadores para o podium
		teleportPlayersToPodium();

	}

	/**
	 * Iniciar novo Nível / Round / Level
	 */
	@Override
	public void levelUp() {

		// limpar targets e moving targets
		// destroyTargets();

		// matar os mobs
		// killEntityTargets();

		if (this.myCloudCraftGame.getLevel().getLevel() >= 1) {
			for (GamePlayer gp: this.livePlayers) {
				Archer archer = (Archer)gp;
				TitleUtil.sendTitle(archer.getPlayer(), 1, 70, 10, "Nível " + this.myCloudCraftGame.getLevel().getLevel(), "");
			}

			// liberar o jogo novamente após 5 segundos
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					myCloudCraftGame.levelUp();
				}
			}, 100L);
		} else {
			this.myCloudCraftGame.levelUp();
		}
	}

	private void destroyTargets() {
		for (Target target : targets) {
			if (target instanceof BlockTarget) {
				BlockTarget bTarget = (BlockTarget) target;
				this.destroyBlockTarget(bTarget);
			}
		}

		for (MovingTarget mTarget : this.movingTargets) {
			this.destroyMovingTarget(mTarget);
		}
	}

	private void teleportPlayersToPodium() {
		Object aList[] = livePlayers.toArray();
		Arrays.sort(aList);
		MGLogger.trace("teleport players to podium - aList.lengh: " + aList.length + "");
	}

	public void addTarget(Target target) {
		targets.add(target);
	}

	public void addMovingTarget(MovingTarget mTarget) {
		movingTargets.add(mTarget);
	}

	public void addEntityTarget(EntityTarget target) {
		livingTargets.add(target);
	}

	public void givePoints(Player player, Integer hitPoints) {
		Archer archer = (Archer)findGamePlayerByPlayer(player);
		archer.addPoints( hitPoints );
		updateScoreBoards();
	}

	public CopyOnWriteArraySet<Target> getTargets() {
		return this.targets;
	}

	public CopyOnWriteArraySet<MovingTarget> getMovingTargets() {
		return this.movingTargets;
	}

	public CopyOnWriteArraySet<EntityTarget> getLivingTargets() {
		return this.livingTargets;
	}

	public void hitTarget(BlockTarget target, Player shooter) {
		targets.remove(target);
		target.hitTarget2(shooter);
		Location loc = target.getBlock().getLocation();
		shooter.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		destroyBlockTarget(target);
		if (shooter != null) {
			Double points = loc.distance(shooter.getLocation());
			
			MGLogger.info("shooter: " + shooter.getName() + " total points: " + points);
			givePoints(shooter, points.intValue());
		}

	}

	public void hitEntityTarget(Target target, Player shooter) {
		target.hitTarget2(shooter);
	}

	public void hitMovingTarget(MovingTarget mTarget, Player shooter) {
		mTarget.hitTarget2(shooter);
		Utils.shootFirework(shooter.getLocation());
		destroyMovingTarget(mTarget);
		if (shooter != null) {
			this.giveBonus(shooter);
		}
	}

	public void giveBonus(Player shooter) {
		Archer archer = (Archer)findGamePlayerByPlayer(shooter);
		if (archer.getBow().equals(ArcherBow.DEFAULT)) {
			archer.setBow(ArcherBow.DOUBLE);
		} else if (archer.getBow().equals(ArcherBow.DOUBLE)) {
			archer.setBow(ArcherBow.TRIPPLE);
		}
	}

	public void destroyMovingTarget(MovingTarget mTarget) {
		Location loc = mTarget.getBlock().getLocation();
		movingTargets.remove(mTarget);
		mTarget.getBlock().setType(Material.AIR);
		if (!mTarget.isHit()) {
			this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		}
	}

	public Location getRandomSpawnLocationForGroundEnemy() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		return locationUtil.getRandomLocationXYZ( this.world, area);
	}

	public Location getRandomSpawnLocationForVerticalMovingTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.FLOATING_AREA);
		return locationUtil.getRandomLocationXYZ( this.world, area);
	}

	public Location getRandomSpawnLocationForGroundTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		return locationUtil.getRandomLocationXYZ( this.world , area);
	}

	public Location getRandomSpawnLocationForFloatingTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.FLOATING_AREA);
		return locationUtil.getRandomLocationXYZ( this.world, area);
	}

	public void hitEntity(Entity entity, Player player) {
		EntityTarget target = findEntityTarget(entity);
		target.setKiller(player);
	}

	public void killPlayer(Player dead) {
		String deadname = dead.getDisplayName();
		Bukkit.broadcastMessage(ChatColor.GOLD + " " + deadname + "" + ChatColor.GREEN + " died.");

		dead.setHealth(20); // Do not show the respawn screen
		dead.getInventory().clear();

		if (this.myCloudCraftGame.isStarted()) {
			this.removeLivePlayer(dead);
			dead.teleport(this.lobby); //TELEPORT DEAD PLAYER TO LOBBY
		}
	}

	public EntityTarget findEntityTargetByZombie(Zombie zombie) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(zombie)) {
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
		}
		return et;
	}

	public EntityTarget findEntityTarget(Entity entity) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(entity)) {
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
		}
		return et;
	}

	public void killEntityTargets() {
		for (EntityTarget eTarget : this.livingTargets) {
			if (eTarget instanceof EntityTarget) {
				this.killEntity(((EntityTarget) eTarget).getLivingEntity()); // this.killZombie...getZombie
			}
		}
	}

	public void killEntity(Entity z) {
		EntityTarget et = (EntityTarget) findEntityTarget(z);
		Location loc = z.getLocation();
		if (et != null) {
			if (et.getKiller() != null) {
				Player player = et.getKiller();
				this.givePoints(player, et.getKillPoints());
				this.livingTargets.remove(et);
			} else {
				if (damageArcherArea(z)) {
					((Damageable) z).damage(((Damageable) z).getMaxHealth());
					this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 2.0F, false, false);
					this.livingTargets.remove(et);
				} else {
					destroyBase(loc.getBlockX());
				}
			}
		}
	}

	/*
	 * public void killZombie(Zombie zombie) { ZombieTarget et = (ZombieTarget)
	 * findEntityTargetByZombie(zombie); Location loc = zombie.getLocation(); if
	 * (et != null) { if (et.getKiller() != null) { Player player =
	 * et.getKiller(); this.givePoints(player, et.getKillPoints());
	 * this.livingTargets.remove(et); } else { if (damageArcherArea(zombie)) {
	 * zombie.damage(zombie.getMaxHealth());
	 * this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 2.0F,
	 * false, false); this.livingTargets.remove(et); } else {
	 * destroyBase(loc.getBlockX()); } } } }
	 */

	private boolean damageArcherArea(Entity entity) {
		Location zl = entity.getLocation();
		Iterator<GamePlayer> it = this.livePlayers.iterator();
		Archer archer = null;

		while (it.hasNext()) {
			archer = (Archer)it.next();
			if (archer.isNear(zl)) {
				break;
			}
		}
		if (archer != null) {
			if (archer.getBaseHealth() <= 0) {
				archer.getBaseBar().setProgress(0);
				return false;
			} else {
				archer.damageBase();
				if (archer.getBaseHealth() > 0) {
					archer.getBaseBar().setProgress(new Double(archer.getBaseHealth()));
				}
			}
		}
		return true;
	}

	private void destroyBase(int x) {
		Location l = new Location(this.world, x, 4, 1169);
		world.getBlockAt(l).setType(Material.AIR);
	}

	@Override
	public boolean isLastLevel() {
		return this.myCloudCraftGame.getLevel().getLevel() == 11;
	}

	public boolean shouldExplodeZombie(Location location) {
		boolean result = false;
		if (location.getBlockX() >= 457 && location.getBlockX() <= 493) {
			if (location.getBlockZ() >= 1170 && location.getBlockZ() <= 1171) {
				result = true;
			}
		}

		return result;
	}

	public void destroyBlockTarget(BlockTarget bTarget) {
		if (bTarget instanceof GroundBlockTarget) {
			destroyGroundBlockTarget(bTarget);
		} else if (bTarget instanceof WallBlockTarget) {
			destroyWallBlockTarget(bTarget);
		} else if (bTarget instanceof FloatingBlockTarget) {
			destrobyFloatingBlockTarget(bTarget);
		}
	}

	private void destrobyFloatingBlockTarget(BlockTarget bTarget) {
		// restaurar o local do target
		Block block = bTarget.getBlock();
		Location l1 = new Location(this.getWorld(), block.getX() - 1, block.getY() - 1, block.getZ());
		Location l2 = new Location(this.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		blockManipulationUtil.clearBlocks(l1, l2);
	}

	public World getWorld() {
		return this.world;
	}

	private void destroyWallBlockTarget(BlockTarget bTarget) {
		// restaurar a parte preta
		Block block = bTarget.getBlock();

		Location l1 = new Location(this.getWorld(), block.getX() - 1, block.getY() - 1, block.getZ());
		Location l2 = new Location(this.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		blockManipulationUtil.createWoolBlocks(l1, l2, DyeColor.BLACK);
	}

	private void destroyGroundBlockTarget(BlockTarget bTarget) {
		Block block = bTarget.getBlock();
		Location l1 = new Location(this.getWorld(), block.getX() - 1, block.getY() - 2, block.getZ());
		Location l2 = new Location(this.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		blockManipulationUtil .clearBlocks(l1, l2);
	}

	public void shootArrows(Player player) {
		int iArrows = 1;
		Archer archer = (Archer)findGamePlayerByPlayer(player);
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

	public void setArena(Arena value) {
		this.arena = value;
	}

	public Integer getConfigIntValue(String name) {
		return (Integer)this.getGameConfigInstance(name);
	}

	public Arena getArena() {
		return this.arena;
	}

	public Object getGameEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	public void setWorld(World world) {
		this.world = world;
	}


}
