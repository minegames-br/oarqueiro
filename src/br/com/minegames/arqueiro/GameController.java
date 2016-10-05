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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import br.com.minegames.arqueiro.command.JoinGameCommand;
import br.com.minegames.arqueiro.command.LeaveGameCommand;
import br.com.minegames.arqueiro.command.StartGameCommand;
import br.com.minegames.arqueiro.command.TeleportToArenaCommand;
import br.com.minegames.arqueiro.command.TriggerFireworkCommand;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.ArcherBow;
import br.com.minegames.arqueiro.domain.Area2D;
import br.com.minegames.arqueiro.domain.Area3D;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.arqueiro.listener.BowShootListener;
import br.com.minegames.arqueiro.listener.EntityHitEvent;
import br.com.minegames.arqueiro.listener.PlayerDeath;
import br.com.minegames.arqueiro.listener.PlayerJoin;
import br.com.minegames.arqueiro.listener.PlayerMove;
import br.com.minegames.arqueiro.listener.PlayerQuit;
import br.com.minegames.arqueiro.listener.ServerListener;
import br.com.minegames.arqueiro.listener.TargetHitEvent;
import br.com.minegames.arqueiro.task.DestroyTargetTask;
import br.com.minegames.arqueiro.task.EndGameTask;
import br.com.minegames.arqueiro.task.ExplodeZombieTask;
import br.com.minegames.arqueiro.task.LevelUpTask;
import br.com.minegames.arqueiro.task.PlaceMovingTargetTask;
import br.com.minegames.arqueiro.task.PlaceTargetTask;
import br.com.minegames.arqueiro.task.SpawnSkeletonTask;
import br.com.minegames.arqueiro.task.SpawnZombieTask;
import br.com.minegames.arqueiro.task.StartCoundDownTask;
import br.com.minegames.arqueiro.task.StartGameTask;
import br.com.minegames.logging.Logger;
import br.com.minegames.util.BlockManipulationUtil;
import br.com.minegames.util.LocationUtil;
import br.com.minegames.util.Region;
import br.com.minegames.util.Utils;
import br.com.minegames.util.title.TitleUtil;

public class GameController extends JavaPlugin {

	private Game game;

	private World world;
	private CopyOnWriteArraySet<Archer> playerList = new CopyOnWriteArraySet<Archer>();
	private CopyOnWriteArraySet<Archer> livePlayers = new CopyOnWriteArraySet<Archer>();
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
	private Runnable endGameTask;
	private int endGameThreadID;
	private Runnable levelUpTask;
	private HashMap<Location, Material> materialsToRestore = new HashMap<Location, Material>();
	private int levelUpThreadID;
	private CopyOnWriteArraySet<Target> targets = new CopyOnWriteArraySet<Target>();
	private CopyOnWriteArraySet<EntityTarget> livingTargets = new CopyOnWriteArraySet<EntityTarget>();
	private CopyOnWriteArraySet<MovingTarget> movingTargets = new CopyOnWriteArraySet<MovingTarget>();

	private CopyOnWriteArraySet<Area2D> arenaSpawnPoints = new CopyOnWriteArraySet<Area2D>();

	private Location lobbyLocation;
	private int maxplayers = 4;
	private int minplayers = 1;
	private int maxZombieSpawned = 5;
	private int maxTarget = 3;
	private int maxMovingTarget = 3;
	private int countDown = 20;
	private long gameStartTime;
	private Runnable startCountDownTask;
	private int startCountDownThreadID;
	private Runnable startGameTask;
	private int startGameThreadID;
	private Runnable explodeZombieTask;
	private int explodeZombieThreadID;

	private Location firstPositionLocation;
	private Location secondPositionLocation;
	private Location thirdPositionLocation;
	private Location fourthPositionLocation;

	private Archer winner;
	private CopyOnWriteArraySet<String> playerNames = new CopyOnWriteArraySet<String>();

	private Area2D spawnArea;
	private Area2D player1Arena;
	private Area2D player2Arena;
	private Area2D player3Arena;
	private Area2D player4Arena;
	private Area2D blackWall;
	private Area3D arena;
	private Area3D floatingArena;

	private Scoreboard scoreboard;

	// private ArcherChest[] playerChest;

	@Override
	public void onEnable() {
		Bukkit.setSpawnRadius(0);
		Bukkit.getConsoleSender().sendMessage(Utils.color("&6O 'O Arqueiro - onEnable"));

		// registrar os Listeners de eventos do servidor e do jogo
		registerListeners();

		// inicializar em que mundo o jogador está. Só deve ter um.
		// não deixar mudar dia/noite
		// não deixar fazer spawn de mobs automatico
		// deixar o horário de dia
		this.world = Bukkit.getWorlds().get(0);
		world.setTime(1000);
		world.setSpawnFlags(false, false);
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doDaylightCycle", "false");

		getCommand("jogar").setExecutor(new JoinGameCommand(this));
		getCommand("iniciar").setExecutor(new StartGameCommand(this));
		getCommand("sair").setExecutor(new LeaveGameCommand(this));
		getCommand("tparena").setExecutor(new TeleportToArenaCommand(this));
		getCommand("fwk").setExecutor(new TriggerFireworkCommand(this));

		Location a1 = new Location(this.getWorld(), 457, 4, 1165);
		Location a2 = new Location(this.getWorld(), 493, 18, 1200);
		this.arena = new Area3D(a1, a2);

		Location f1 = new Location(this.getWorld(), 459, 10, 1170);
		Location f2 = new Location(this.getWorld(), 491, 14, 1197);
		this.floatingArena = new Area3D(f1, f2);

		Location b1 = new Location(this.getWorld(), 457, 6, 1200);
		Location b2 = new Location(this.getWorld(), 493, 18, 1200);
		this.blackWall = new Area2D(b1, b2);

		Location s1 = new Location(this.getWorld(), 460, 6, 1180);
		Location s2 = new Location(this.getWorld(), 490, 6, 1200);
		this.spawnArea = new Area2D(s1, s2);

		this.player1Arena = (new Area2D(new Location(this.getWorld(), 457, a1.getY(), a1.getZ()),
				new Location(this.getWorld(), 466, a1.getY(), 1169)));
		this.player2Arena = new Area2D(new Location(this.getWorld(), 468, a1.getY(), a1.getZ()),
				new Location(this.getWorld(), 475, a1.getY(), 1169));
		this.player3Arena = new Area2D(new Location(this.getWorld(), 477, a1.getY(), a1.getZ()),
				new Location(this.getWorld(), 484, a1.getY(), 1169));
		this.player4Arena = new Area2D(new Location(this.getWorld(), 486, a1.getY(), 1164),
				new Location(this.getWorld(), 493, a1.getY(), 1169));

		this.arenaSpawnPoints.clear();
		this.arenaSpawnPoints.add(this.player1Arena);
		this.arenaSpawnPoints.add(this.player2Arena);
		this.arenaSpawnPoints.add(this.player3Arena);
		this.arenaSpawnPoints.add(this.player4Arena);

		this.lobbyLocation = new Location(this.getWorld(), 530, 4, 1210);

		this.firstPositionLocation = new Location(this.getWorld(), 551, 19, 1227);
		this.secondPositionLocation = new Location(this.getWorld(), 557, 14, 1220);
		this.thirdPositionLocation = new Location(this.getWorld(), 564, 10, 1227);
		this.fourthPositionLocation = new Location(this.getWorld(), 557, 5, 1227);

		for (int i = arena.getPointA().getBlockX(); i < arena.getPointB().getBlockX(); i++) {
			Location l1 = new Location(this.world, i, 4, 1168);
			Material m1 = world.getBlockAt(l1).getType();
			this.materialsToRestore.put(l1, m1);

			Location l2 = new Location(this.world, i, 4, 1169);
			Material m2 = world.getBlockAt(l2).getType();
			this.materialsToRestore.put(l2, m2);
		}

		init();
	}

	private void init() {

		this.game = new Game();

		// zerar lista de players
		this.playerList.clear();
		this.livePlayers.clear();
		this.playerNames.clear();

		// esse target vai ser usado durante o jogo. Dinamicamente vai ser
		// criado.
		// quando for acertado vai desaparecer e outro será criado e associado
		this.targets.clear();
		this.livingTargets.clear();
		this.movingTargets.clear();

		// Remover qualquer entidade que tenha ficado no mapa
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}

		// inicializar variaveis de instancia
		this.maxZombieSpawned = 5;
		this.placeTargetTask = new PlaceTargetTask(this);
		this.placeMovingTargetTask = new PlaceMovingTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.endGameTask = new EndGameTask(this);
		this.levelUpTask = new LevelUpTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.spawnSkeletonTask = new SpawnSkeletonTask(this);
		this.startCountDownTask = new StartCoundDownTask(this);
		this.startGameTask = new StartGameTask(this);
		//this.spawnZombieTask = new SpawnZombieTask(this);
		this.explodeZombieTask = new ExplodeZombieTask(this);

		this.countDown = 10;
		this.winner = null;

		// re-criar a parede preta no fundo da arena em que aparecem os targets
		createBlackWall();

		// re-criar proteção do player (cerca)
		Iterator<Location> it = materialsToRestore.keySet().iterator();
		while (it.hasNext()) {
			Location l = it.next();
			Material m = materialsToRestore.get(l);
			world.getBlockAt(l).setType(m);
		}

		// Agendar as threads que vão detectar se o jogo pode comecar
		BukkitScheduler scheduler = getServer().getScheduler();
		this.startGameThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startGameTask, 0L, 20L);
		this.startCountDownThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startCountDownTask, 0L, 25L);

	}

	@Override
	public void onDisable() {
		if (this.game.isStarted()) {
			this.game.shutDown();
			this.endGame();
		}
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new PlayerMove(this), this);
		pm.registerEvents(new PlayerQuit(this), this);
		pm.registerEvents(new PlayerDeath(this), this);
		pm.registerEvents(new TargetHitEvent(this), this);
		pm.registerEvents(new EntityHitEvent(this), this);
		pm.registerEvents(new ServerListener(this), this);
		pm.registerEvents(new BowShootListener(this), this);
	}

	/*
	 * Quando esse método rodar, já teremos a quantidade máxima de jogadores na
	 * arena ou então a quantidade mínima e o tempo de espera terminou.
	 */
	public void startGameEngine() {
		this.game.start();

		Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.startGameEngine"));

		// preparar Score Board
		int loc = 0;
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			Area2D spawnPoint = ((Area2D) this.arenaSpawnPoints.toArray()[loc]);
			archer.setSpawnPoint(spawnPoint);
			player.teleport(spawnPoint.getMiddle());
			archer.regainHealthToPlayer(archer);

			// Preparar o jogador para a rodada. Dar armaduras, armas, etc...
			archer.setBow(ArcherBow.DEFAULT);

			BossBar bar = createBossBar();
			archer.addBaseBar(bar);
			bar.addPlayer(player);

			Logger.log("preparar score board archer: " + archer.getPlayer().getName() + " base: "
					+ new Double(archer.getBaseHealth()));

			setupPlayerToStartGame(player);
			loc++;
		}

		updateScoreBoards();

		BukkitScheduler scheduler = getServer().getScheduler();

		// Terminar threads de preparacao do jogo
		scheduler.cancelTask(startCountDownThreadID);
		scheduler.cancelTask(startGameThreadID);

		// Iniciar threads do jogo
		this.placeTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.placeTargetTask, 0L, 50L);
		this.placeMovingTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.placeMovingTargetTask, 200L,
				30L);
		this.destroyTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.destroyTargetTask, 0L, 100L);
		this.endGameThreadID = scheduler.scheduleSyncRepeatingTask(this, this.endGameTask, 0L, 50L);
		this.spawnZombieThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnZombieTask, 0L, 50L);
		this.spawnSkeletonThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnSkeletonTask, 0L, 150L);
		this.levelUpThreadID = scheduler.scheduleSyncRepeatingTask(this, this.levelUpTask, 0L, 100L);
		// this.explodeZombieThreadID =
		// scheduler.scheduleSyncRepeatingTask(this, this.explodeZombieTask, 0L,
		// 20L);

	}

	private BossBar createBossBar() {
		BossBar bar = Bukkit.createBossBar("Base", BarColor.PINK, BarStyle.SOLID);
		bar.setProgress(1F);
		return bar;
	}

	private void updateScoreBoards() {
		for (Archer archer : this.livePlayers) {
			Player player = archer.getPlayer();
			Scoreboard scoreboard = player.getScoreboard();
			for (Archer a1 : this.livePlayers) {
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

		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 500);
		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 100);

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
	public void endGame() {
		if (this.game.isStarted()) {
			this.game.endGame();
		}
		Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.endGame"));

		// Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.placeMovingTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.endGameThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnSkeletonThreadID);
		Bukkit.getScheduler().cancelTask(this.levelUpThreadID);
		Bukkit.getScheduler().cancelTask(this.explodeZombieThreadID);

		// TODO o que vai acontecer com os jogadores quando acabar o jogo?
		// por enquanto vou tirá-los da arena e zerar os inventarios e recriar a
		// parede preta
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			player.sendMessage("Você fez " + archer.getPoint() + " pontos.");
			archer.regainHealthToPlayer(archer);
		}

		// destroyTargets()
		destroyTargets();

		// restaurar parede preta
		createBlackWall();

		// manda os jogadores para o podium
		teleportPlayersToPodium();

		// remover as bossbars
		removeBossBars();
		// mandar os jogadores de volta para o lobby
		// teleportPlayersBackToLobby();

		// limpar inventario do jogador
		clearPlayersInventory();

		if (!this.game.isShuttingDown()) {
			// limpar a arena e reiniciar o plugin
			init();
		}

	}

	private void removeBossBars() {
		for (Archer archer : livePlayers) {
			archer.getBaseBar().removeAll();
		}
	}

	private void removeBossBar(Player player) {
		Archer archer = findArcherByPlayer(player);
		archer.getBaseBar().removeAll();
	}

	private void removeBossBar(Archer archer) {
		archer.getBaseBar().removeAll();
	}

	private void clearPlayersInventory() {
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			this.clearPlayerInventory(player);
		}
	}

	private void clearPlayerInventory(Player player) {
		PlayerInventory inventory = player.getInventory();

		inventory.clear();
		inventory.setArmorContents(null);
	}

	/**
	 * Iniciar novo Nível / Round / Level
	 */
	public void levelUp() {

		// limpar targets e moving targets
		// destroyTargets();

		// matar os mobs
		// killEntityTargets();

		if (this.game.getLevel().getLevel() >= 1) {
			for (Archer archer : this.livePlayers) {
				TitleUtil.sendTitle(archer.getPlayer(), 1, 70, 10, "Nível " + this.game.getLevel().getLevel(), "");

				/*
				 * //aumentar a força dos arcos Player player =
				 * archer.getPlayer(); player.getInventory().clear(); ItemStack
				 * it = new ItemStack(Material.BOW); if(
				 * (this.game.getLevel().getLevel() % 2) == 0 ) {
				 * //it.addEnchantment(Enchantment.ARROW_DAMAGE,
				 * this.game.getLevel().getLevel()/2); }
				 * it.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				 * player.getInventory().addItem(it);
				 * player.getInventory().addItem(new ItemStack(Material.ARROW));
				 */
			}

			// liberar o jogo novamente após 5 segundos
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					game.levelUp();
				}
			}, 100L);
		} else {
			this.game.levelUp();
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
		Logger.log(aList.length + "");

		for (int i = 0; i < aList.length; i++) {

			Archer a = (Archer) aList[i];

			if (i == 0) {
				Player player = a.getPlayer();
				player.teleport(this.firstPositionLocation);
				Utils.shootFirework(player);
			}

			if (i == 1) {
				Player player = a.getPlayer();
				player.teleport(this.secondPositionLocation);
				Utils.shootFirework(player);
			}

			if (i == 2) {
				Player player = a.getPlayer();
				player.teleport(this.thirdPositionLocation);
				Utils.shootFirework(player);
			}

			if (i == 3) {
				Player player = a.getPlayer();
				player.teleport(this.fourthPositionLocation);
				Utils.shootFirework(player);
			}

		}
	}

	private void teleportPlayersBackToLobby() {
		// TODO pegar o location do lobby numa configuracao
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			player.teleport(lobbyLocation);
		}
	}

	// TODO recuperar a parede de alguma configuração
	private void createBlackWall() {
		BlockManipulationUtil.createWoolBlocks(this.blackWall.getPointA(), this.blackWall.getPointB(), DyeColor.BLACK);
	}

	public void addPlayer(Player player) {
		Archer archer = null;
		if (findArcherByPlayer(player) == null) {
			archer = new Archer();
			archer.setPlayer(player);
			playerList.add(archer);
			livePlayers.add(archer);
			player.sendMessage(Utils.color("&aBem vindo, Arqueiro!"));
			playerNames.add(player.getName());
		} else {
			Logger.log("Jogador já está na lista");
		}
	}

	public void removeLivePlayer(Player player) {
		Archer archer = findArcherByPlayer(player);

		if (archer != null) {
			if (player != null) {
				player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
				player.getInventory().clear();
				player.teleport(lobbyLocation);
			}
			removeBossBar(archer);
			livePlayers.remove(archer);
		}

		if (livePlayers.size() == 0) {
			this.game.endGame();
			this.endGame();
		}
	}

	public CopyOnWriteArraySet<Archer> getLivePlayers() {
		return this.livePlayers;
	}

	public World getWorld() {
		return this.world;
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

	public int getMaxPlayers() {
		return this.maxplayers;
	}

	public int getMinPlayers() {
		return this.minplayers;
	}

	public int getCoundDown() {
		// TODO Auto-generated method stub
		return countDown;
	}

	public Archer getWinner() {
		return winner;
	}

	public void setWinner(Archer winner) {
		this.winner = winner;
	}

	public Archer findArcherByPlayer(Player player) {
		for (Archer archer : playerList) {
			if (archer.getPlayer().equals(player)) {
				return archer;
			}
		}
		return null;
	}

	public void startCoundDown() {
		this.game.startCountDown();
	}

	public void proceedCountdown() {
		if (this.countDown != 0) {
			this.countDown--;
			Bukkit.broadcastMessage(Utils.color("&6O jogo vai começar em " + this.countDown + " ..."));
		} else {
			Bukkit.getScheduler().cancelTask(startCountDownThreadID);
		}
	}

	public void givePoints(Player player, int hitPoints) {
		Archer archer = findArcherByPlayer(player);
		archer.addPoints(hitPoints);
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

	public Area2D getSpawnArea() {
		return spawnArea;
	}

	public void setSpawnArea(Area2D spawnArea) {
		this.spawnArea = spawnArea;
	}

	public void hitTarget(BlockTarget target, Player shooter) {
		targets.remove(target);
		if (shooter != null) {
			givePoints(shooter, target.getHitPoints());
		}
		target.hitTarget2(shooter);
		Location loc = target.getBlock().getLocation();
		this.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		destroyBlockTarget(target);
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
		Archer archer = findArcherByPlayer(shooter);
		if (archer.getBow().equals(ArcherBow.DEFAULT)) {
			archer.setBow(ArcherBow.DOUBLE);
		} else if (archer.getBow().equals(ArcherBow.DOUBLE)) {
			archer.setBow(ArcherBow.TRIPPLE);
		}
	}

	public void destroyMovingTarget(MovingTarget mTarget) {
		Location loc = mTarget.getBlock().getLocation();
		Logger.log("destroyMovingTarget: " + loc);
		movingTargets.remove(mTarget);
		mTarget.getBlock().setType(Material.AIR);
		if (!mTarget.isHit()) {
			this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		}
	}

	public void sendToLobby(Player player) {
		player.teleport(lobbyLocation);
	}

	public Location getRandomSpawnLocationForGroundEnemy() {
		return LocationUtil.getRandomLocationXZ(world, this.spawnArea);
	}

	public Location getRandomSpawnLocationForVerticalMovingTarget() {
		return LocationUtil.getRandomLocationXYZ(world, this.floatingArena);
	}

	public Location getRandomSpawnLocationForGroundTarget() {
		return LocationUtil.getRandomLocationXZ(world, this.spawnArea);
	}

	public Location getRandomSpawnLocationForWallTarget() {
		return LocationUtil.getRandomLocationXY(world, this.blackWall);
	}

	public Location getRandomSpawnLocationForFloatingTarget() {
		return LocationUtil.getRandomLocationXYZ(world, this.floatingArena);
	}

	public void hitZombie(Zombie entity, Player player) {
		Archer archer = this.findArcherByPlayer(player);
		EntityTarget targetZombie = findEntityTargetByZombie(entity);
		targetZombie.setKiller(player);
	}

	public int getMaxZombieSpawned() {
		return this.maxZombieSpawned;
	}

	public void setMaxZombieSpawned(int value) {
		this.maxZombieSpawned = value;
	}

	public void killPlayer(Player dead) {
		String deadname = dead.getDisplayName();
		Bukkit.broadcastMessage(ChatColor.GOLD + " " + deadname + "" + ChatColor.GREEN + " died.");

		dead.setHealth(20); // Do not show the respawn screen
		dead.getInventory().clear();

		if (this.game.isStarted()) {
			this.removeLivePlayer(dead);
		}
	}

	public EntityTarget findEntityTargetByZombie(Zombie zombie) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(zombie)) {
				Logger.log("zombie was a target");
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
			Logger.log("zombie was a NOT target");
		}
		return et;
	}

	public void killEntityTargets() {
		for (EntityTarget eTarget : this.livingTargets) {
			if (eTarget instanceof ZombieTarget) {
				this.killZombie(((ZombieTarget) eTarget).getZombie());
			}
		}
	}

	public void killZombie(Zombie zombie) {
		ZombieTarget et = (ZombieTarget) findEntityTargetByZombie(zombie);
		Location loc = zombie.getLocation();
		if (et != null) {
			if (et.getKiller() != null) {
				Player player = et.getKiller();
				this.givePoints(player, et.getKillPoints());
				this.livingTargets.remove(et);
			} else {
				if (damageArcherArea(zombie)) {
					zombie.damage(zombie.getMaxHealth());
					this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 2.0F, false, false);
					this.livingTargets.remove(et);
				} else {
					destroyBase(loc.getBlockX());
				}
			}
		}
	}

	private boolean damageArcherArea(Zombie zombie) {
		Location zl = zombie.getLocation();
		Iterator<Archer> it = this.livePlayers.iterator();
		Archer archer = null;

		while (it.hasNext()) {
			archer = it.next();
			Logger.log("archer: " + archer.getPlayer().getName() + " base: " + new Double(archer.getBaseHealth()));
			if (archer.isNear(zl)) {
				break;
			}
		}
		if (archer != null) {
			Logger.log("base: " + new Double(archer.getBaseHealth()));
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

	public Game getGame() {
		return this.game;
	}

	public boolean isLastLevel() {
		return this.game.getLevel().getLevel() == 11;
	}

	public int getMaxTarget() {
		return this.maxTarget;
	}

	public boolean shouldExplodeZombie(Location location) {
		Region r = new Region("spawnLocation", new Location(this.world, 457, 3, 1170),
				new Location(this.world, 493, 3, 1171));

		boolean result = false;
		Logger.log(location + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
		if (location.getBlockX() >= 457 && location.getBlockX() <= 493) {
			if (location.getBlockZ() >= 1170 && location.getBlockZ() <= 1171) {
				result = true;
			}
		}

		return result;
	}

	public int getMaxMovingTarget() {
		return this.maxMovingTarget;
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
		Archer archer = findArcherByPlayer(player);
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

			Logger.log("i " + i + " spread " + spread + " pitch " + pitch + " yaw " + yaw + " x " + x + " y " + y
					+ " z " + z + " z_axis " + z_axis);

			Vector vector = new Vector(x, z, y);
			vector.multiply(3);

			Arrow a = player.getWorld().spawn(player_location, Arrow.class);
			// a.setVelocity(vector);

			player.launchProjectile(Arrow.class, vector);
		}
	}

}
