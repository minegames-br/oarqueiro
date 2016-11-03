package br.com.minegames.arqueiro;

import java.util.Arrays;
import java.util.HashMap;
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
import br.com.minegames.core.domain.Area3D;
import br.com.minegames.core.domain.Arena;
import br.com.minegames.core.domain.Game;
import br.com.minegames.core.logging.MGLogger;
import br.com.minegames.core.util.BlockManipulationUtil;
import br.com.minegames.core.util.LocationUtil;
import br.com.minegames.core.util.Region;
import br.com.minegames.core.util.Utils;
import br.com.minegames.core.util.title.TitleUtil;
import br.com.minegames.gamemanager.domain.GamePlayer;
import br.com.minegames.gamemanager.plugin.MyCloudCraftPlugin;

public class GameController extends MyCloudCraftPlugin {

	private TheLastArcher theLastArcher;

	private Runnable placeTargetTask;
	private int placeTargetThreadID;
	private Runnable placeMovingTargetTask;
	private int placeMovingTargetThreadID;
	private Runnable destroyTargetTask;
	private int destroyTargetThreadID;
	private Runnable spawnZombieTask;
	private int spawnZombieThreadID;
	private Runnable spawnSkeletonTask;
	private int spawnSkeletonThreadID;

	private HashMap<Location, Material> materialsToRestore = new HashMap<Location, Material>();
	private CopyOnWriteArraySet<Target> targets = new CopyOnWriteArraySet<Target>();
	private CopyOnWriteArraySet<EntityTarget> livingTargets = new CopyOnWriteArraySet<EntityTarget>();
	private CopyOnWriteArraySet<MovingTarget> movingTargets = new CopyOnWriteArraySet<MovingTarget>();

	private Runnable explodeZombieTask;
	private int explodeZombieThreadID;

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.setSpawnRadius(0);
		Bukkit.getLogger().info("O Arqueiro - onEnable");
		
		//ao criar, o jogo fica imediatamente esperando jogadores
		this.theLastArcher = new TheLastArcher();

	}

	protected void restart() {
		super.restart();
		// esse target vai ser usado durante o jogo. Dinamicamente vai ser
		// criado.
		// quando for acertado vai desaparecer e outro será criado e associado
		this.targets.clear();
		this.livingTargets.clear();
		this.movingTargets.clear();

	}
	
	@Override
	public void init(Game game, Arena arena) {
		super.init(game, arena);

		// inicializar variaveis de instancia
		this.placeTargetTask = new PlaceTargetTask(this);
		this.placeMovingTargetTask = new PlaceMovingTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.spawnSkeletonTask = new SpawnSkeletonTask(this);
		// this.spawnZombieTask = new SpawnZombieTask(this);
		this.explodeZombieTask = new ExplodeZombieTask(this);

	}

	@Override
	protected void start() {
		super.start();
		this.theLastArcher = new TheLastArcher();
	}

	@Override
	public void onDisable() {
		if (this.theLastArcher.isStarted()) {
			this.theLastArcher.shutDown();
			this.endGame();
		}
	}

	protected void registerListeners() {
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
		start();
		
		this.theLastArcher.start();

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
			Bukkit.getLogger().info("spawn point " + loc + " " + spawnPoint.getPointA() + " / " + spawnPoint.getPointB() );
			archer.setSpawnPoint(spawnPoint);							   
			player.teleport(LocationUtil.getMiddle(this.world, spawnPoint) );
			archer.regainHealthToPlayer(archer);

			// Preparar o jogador para a rodada. Dar armaduras, armas, etc...
			archer.setBow(ArcherBow.DEFAULT);

			BossBar bar = createBossBar();
			archer.addBaseBar(bar);
			bar.addPlayer(player);

			MGLogger.debug("preparar score board archer: " + archer.getPlayer().getName() + " base: "
					+ new Double(archer.getBaseHealth()));

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

	}
	
	public boolean shouldEndGame() {
    	//Terminar o jogo após o 10 Nível
    	if(this.theLastArcher.getLevel().getLevel() >= 11 && this.theLastArcher.isStarted()) {
            Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - Time is Over"));
            return true;
    	}
    	
    	//Terminar o jogo caso não tenha mais jogadores
    	if( this.getLivePlayers().size() == 0  && theLastArcher.isStarted()) {
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
		if (this.theLastArcher.isStarted()) {
			this.theLastArcher.endGame();
		}
		MGLogger.info("Game.endGame");

		// Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.placeMovingTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnSkeletonThreadID);
		Bukkit.getScheduler().cancelTask(this.explodeZombieThreadID);

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
	public void levelUp() {

		// limpar targets e moving targets
		// destroyTargets();

		// matar os mobs
		// killEntityTargets();

		if (this.theLastArcher.getLevel().getLevel() >= 1) {
			for (GamePlayer gp: this.livePlayers) {
				Archer archer = (Archer)gp;
				TitleUtil.sendTitle(archer.getPlayer(), 1, 70, 10, "Nível " + this.theLastArcher.getLevel().getLevel(), "");
			}

			// liberar o jogo novamente após 5 segundos
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					theLastArcher.levelUp();
				}
			}, 100L);
		} else {
			this.theLastArcher.levelUp();
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

	private void teleportPlayersBackToLobby() {
		// TODO pegar o location do lobby numa configuracao
		for (GamePlayer gp : livePlayers) {
			Archer archer = (Archer)gp;
			Player player = archer.getPlayer();
			teleportPlayersBackToLobby(player);
		}
	}

	/*
	// TODO recuperar a parede de alguma configuração
	private void createBlackWall() {
		BlockManipulationUtil.createWoolBlocks(this.blackWall.getPointA(), this.blackWall.getPointB(), DyeColor.BLACK);
	}
	*/

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
		MGLogger.info("destroyMovingTarget - Location " + loc);
		movingTargets.remove(mTarget);
		mTarget.getBlock().setType(Material.AIR);
		if (!mTarget.isHit()) {
			this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		}
	}

	public Location getRandomSpawnLocationForGroundEnemy() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		Bukkit.getLogger().info("zombie spawn area: A (" + area.getPointA() + ") B (" + area.getPointB() + ")"); 
		return LocationUtil.getRandomLocationXYZ( this.world, area);
	}

	public Location getRandomSpawnLocationForVerticalMovingTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.FLOATING_AREA);
		return LocationUtil.getRandomLocationXYZ( this.world, area);
	}

	public Location getRandomSpawnLocationForGroundTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.MONSTERS_SPAWN_AREA);
		return LocationUtil.getRandomLocationXYZ( this.world , area);
	}

	public Location getRandomSpawnLocationForFloatingTarget() {
		Area3D area = (Area3D)getGameArenaConfig(Constants.FLOATING_AREA);
		Bukkit.getLogger().info("floating target area: A (" + area.getPointA() + ") B (" + area.getPointB() + ")"); 
		return LocationUtil.getRandomLocationXYZ( this.world, area);
	}

	public void hitEntity(Entity entity, Player player) {
		Archer archer = (Archer)this.findGamePlayerByPlayer(player);
		EntityTarget target = findEntityTarget(entity);
		target.setKiller(player);
	}

	public void killPlayer(Player dead) {
		String deadname = dead.getDisplayName();
		Bukkit.broadcastMessage(ChatColor.GOLD + " " + deadname + "" + ChatColor.GREEN + " died.");

		dead.setHealth(20); // Do not show the respawn screen
		dead.getInventory().clear();

		if (this.theLastArcher.isStarted()) {
			this.removeLivePlayer(dead);
		}
	}

	public EntityTarget findEntityTargetByZombie(Zombie zombie) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(zombie)) {
				MGLogger.debug("zombie was a target");
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
			MGLogger.debug("zombie was a NOT target");
		}
		return et;
	}

	public EntityTarget findEntityTarget(Entity entity) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(entity)) {
				MGLogger.debug("entity was a target");
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
			MGLogger.debug("entity was a NOT target");
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
				MGLogger.debug("killEntity: killer not null");
				Player player = et.getKiller();
				this.givePoints(player, et.getKillPoints());
				this.livingTargets.remove(et);
			} else {
				MGLogger.debug("killEntity: killer null");
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
			MGLogger.debug("damageArcherArea - archer: " + archer.getPlayer().getName() + " base: " + new Double(archer.getBaseHealth()));
			if (archer.isNear(zl)) {
				break;
			}
		}
		if (archer != null) {
			MGLogger.debug("damageArcherArea - base: " + new Double(archer.getBaseHealth()));
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

	public TheLastArcher getTheLastArcher() {
		return this.theLastArcher;
	}

	public boolean isLastLevel() {
		return this.theLastArcher.getLevel().getLevel() == 11;
	}

	public boolean shouldExplodeZombie(Location location) {
		Region r = new Region("spawnLocation", new Location(this.world, 457, 3, 1170),
				new Location(this.world, 493, 3, 1171));

		boolean result = false;
		MGLogger.debug("shouldExplodeZombie - " + location + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
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
		BlockManipulationUtil.clearBlocks(l1, l2);
	}

	public World getWorld() {
		return this.world;
	}

	private void destroyWallBlockTarget(BlockTarget bTarget) {
		// restaurar a parte preta
		Block block = bTarget.getBlock();

		Location l1 = new Location(this.getWorld(), block.getX() - 1, block.getY() - 1, block.getZ());
		Location l2 = new Location(this.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		BlockManipulationUtil.createWoolBlocks(l1, l2, DyeColor.BLACK);
	}

	private void destroyGroundBlockTarget(BlockTarget bTarget) {
		Block block = bTarget.getBlock();
		Location l1 = new Location(this.getWorld(), block.getX() - 1, block.getY() - 2, block.getZ());
		Location l2 = new Location(this.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		BlockManipulationUtil.clearBlocks(l1, l2);
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

			MGLogger.debug("shootArrows - " + i + " spread " + spread + " pitch " + pitch + " yaw " + yaw + " x " + x + " y " + y
					+ " z " + z + " z_axis " + z_axis);

			Vector vector = new Vector(x, z, y);
			vector.multiply(3);

			Arrow a = player.getWorld().spawn(player_location, Arrow.class);
			// a.setVelocity(vector);

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
