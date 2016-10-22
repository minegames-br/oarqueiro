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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;
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
import br.com.minegames.core.domain.Area3D;
import br.com.minegames.core.domain.Config;
import br.com.minegames.core.domain.GameInstance;
import br.com.minegames.core.domain.Local;
import br.com.minegames.core.logging.Logger;
import br.com.minegames.core.util.BlockManipulationUtil;
import br.com.minegames.core.util.LocationUtil;
import br.com.minegames.core.util.Region;
import br.com.minegames.core.util.Utils;
import br.com.minegames.core.util.title.TitleUtil;
import br.com.minegames.gamemanager.client.delegate.GameDelegate;

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

	private long gameStartTime;
	private Runnable startCountDownTask;
	private int startCountDownThreadID;
	private Runnable startGameTask;
	private int startGameThreadID;
	private Runnable explodeZombieTask;
	private int explodeZombieThreadID;
	private int countDown;
	private GameInstance instance = new GameInstance();

	private Archer winner;
	private CopyOnWriteArraySet<String> playerNames = new CopyOnWriteArraySet<String>();

	private Scoreboard scoreboard;

	private String arena;

	private GameDelegate delegate;

	// private ArcherChest[] playerChest;

	public GameInstance getGameInstance() {
		return this.instance;
	}
	
	@Override
	public void onEnable() {
		Bukkit.setSpawnRadius(0);
		Logger.info("O Arqueiro - onEnable");
		
		// inicializar em que mundo o jogador est�. S� deve ter um.
		// n�o deixar mudar dia/noite
		// n�o deixar fazer spawn de mobs automatico
		// deixar o hor�rio de dia
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

		this.delegate = GameDelegate.getInstance();
		
		//ao criar, o jogo fica imediatamente esperando jogadores
		this.game = new Game();

		this.countDown = Integer.parseInt(this.delegate.getGlobalConfig(Constants.START_COUNTDOWN).getValue());
		Logger.info(Constants.START_COUNTDOWN + " " + this.countDown );
		if(this.countDown == 0) {
			this.countDown = 10;
		}

		// Agendar as threads que v�o detectar se o jogo pode comecar
		this.startCountDownTask = new StartCoundDownTask(this);
		this.startGameTask = new StartGameTask(this);

		BukkitScheduler scheduler = getServer().getScheduler();
		this.startGameThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startGameTask, 0L, 20L);
		this.startCountDownThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startCountDownTask, 0L, 25L);

		// inicializar variaveis de instancia
		this.placeTargetTask = new PlaceTargetTask(this);
		this.placeMovingTargetTask = new PlaceMovingTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.endGameTask = new EndGameTask(this);
		this.levelUpTask = new LevelUpTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.spawnSkeletonTask = new SpawnSkeletonTask(this);
		// this.spawnZombieTask = new SpawnZombieTask(this);
		this.explodeZombieTask = new ExplodeZombieTask(this);

	}

	private void restart() {
		// zerar lista de players
		this.playerList.clear();
		this.livePlayers.clear();
		this.playerNames.clear();

		// esse target vai ser usado durante o jogo. Dinamicamente vai ser
		// criado.
		// quando for acertado vai desaparecer e outro ser� criado e associado
		this.targets.clear();
		this.livingTargets.clear();
		this.movingTargets.clear();

		// Remover qualquer entidade que tenha ficado no mapa
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}

		this.winner = null;

		// re-criar prote��o do player (cerca)
		Iterator<Location> it = materialsToRestore.keySet().iterator();
		while (it.hasNext()) {
			Location l = it.next();
			Material m = materialsToRestore.get(l);
			world.getBlockAt(l).setType(m);
		}

		this.countDown = Integer.parseInt(this.delegate.getGlobalConfig(Constants.START_COUNTDOWN).getValue());
		Logger.info(Constants.START_COUNTDOWN + " " + this.countDown );
		if(this.countDown == 0) {
			this.countDown = 10;
		}
		
		BukkitScheduler scheduler = getServer().getScheduler();
		this.startGameThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startGameTask, 0L, 20L);
		this.startCountDownThreadID = scheduler.scheduleSyncRepeatingTask(this, this.startCountDownTask, 0L, 25L);

	}

	private void start() {
		this.game = new Game();
		
		this.instance = GameDelegate.getInstance().joinGame(this.arena);
		
		this.world = Bukkit.getWorld(this.instance.getWorld().getName());

		// Remover qualquer entidade que tenha ficado no mapa
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}

		this.countDown = Integer.parseInt(this.delegate.getGlobalConfig(Constants.START_COUNTDOWN).getValue());
		Logger.info(Constants.START_COUNTDOWN + " " + this.countDown );
		if(this.countDown == 0) {
			this.countDown = 10;
		}
		
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

	public int getCountDown() {
		return this.countDown;
	}
	
	/*
	 * Quando esse m�todo rodar, j� teremos a quantidade m�xima de jogadores na
	 * arena ou ent�o a quantidade m�nima e o tempo de espera terminou.
	 */
	public void startGameEngine() {
		start();
		
		this.game.start();

		// registrar os Listeners de eventos do servidor e do jogo
		registerListeners();

		Logger.info("Game.startGameEngine");

		// preparar Score Board
		int loc = 0;
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			Area3D spawnPoint = (Area3D)this.instance.getAreaListByType("PLAYER-SPAWN").toArray()[loc];
			archer.setSpawnPoint(spawnPoint);							   
			player.teleport(LocationUtil.getMiddle(this.world, spawnPoint) );
			archer.regainHealthToPlayer(archer);

			// Preparar o jogador para a rodada. Dar armaduras, armas, etc...
			archer.setBow(ArcherBow.DEFAULT);

			BossBar bar = createBossBar();
			archer.addBaseBar(bar);
			bar.addPlayer(player);

			Logger.debug("preparar score board archer: " + archer.getPlayer().getName() + " base: "
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
		// scheduler.scheduleSyncRepeatingTask(this, this.explodeZombieTask, 0L, 20L);

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
	 * Nesse m�todo poderemos decidir o que dar a cada jogador
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
	 * Quando esse m�todo executar, o jogo ter� terminado com um vencedor e/ou o
	 * tempo ter� acabado.
	 */
	public void endGame() {
		if (this.game.isStarted()) {
			this.game.endGame();
		}
		Logger.info("Game.endGame");

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
		// por enquanto vou tir�-los da arena e zerar os inventarios e recriar a
		// parede preta
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			player.sendMessage("Voc� fez " + archer.getPoint() + " pontos.");
			archer.regainHealthToPlayer(archer);
		}

		// destroyTargets()
		destroyTargets();

		// restaurar parede preta
		//createBlackWall();

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
			restart();
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
	 * Iniciar novo N�vel / Round / Level
	 */
	public void levelUp() {

		// limpar targets e moving targets
		// destroyTargets();

		// matar os mobs
		// killEntityTargets();

		if (this.game.getLevel().getLevel() >= 1) {
			for (Archer archer : this.livePlayers) {
				TitleUtil.sendTitle(archer.getPlayer(), 1, 70, 10, "N�vel " + this.game.getLevel().getLevel(), "");
			}

			// liberar o jogo novamente ap�s 5 segundos
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
		Logger.trace("teleport players to podium - aList.lengh: " + aList.length + "");
		/*
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
		*/
	}

	private void teleportPlayersBackToLobby() {
		// TODO pegar o location do lobby numa configuracao
		for (Archer archer : livePlayers) {
			Player player = archer.getPlayer();
			teleportPlayersBackToLobby(player);
		}
	}

	public void teleportPlayersBackToLobby(Player player) {
		
		Local lobbyLocal = this.instance.getLocalByConfig(Constants.LOBBY_LOCATION);
		Logger.info("teleportPlayersBackToLobby - lobby local: " + lobbyLocal);
		Location l = Utils.toLocation(this.world, lobbyLocal);
		player.teleport(l);
	}
	
	/*
	// TODO recuperar a parede de alguma configura��o
	private void createBlackWall() {
		BlockManipulationUtil.createWoolBlocks(this.blackWall.getPointA(), this.blackWall.getPointB(), DyeColor.BLACK);
	}
	*/

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
			Logger.debug("Jogador j� est� na lista");
		}
	}

	public void removeLivePlayer(Player player) {
		Archer archer = findArcherByPlayer(player);

		if (archer != null) {
			if (player != null) {
				player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
				player.getInventory().clear();
				teleportPlayersBackToLobby(player);
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
			Bukkit.broadcastMessage(Utils.color("&6O jogo vai come�ar em " + this.countDown + " ..."));
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

	public void hitTarget(BlockTarget target, Player shooter) {
		targets.remove(target);
		target.hitTarget2(shooter);
		Location loc = target.getBlock().getLocation();
		this.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		destroyBlockTarget(target);
		if (shooter != null) {
			float pX = (float) (loc.getBlock().getX() - shooter.getPlayer().getLocation().getX());
			float pY = (float) (loc.getBlock().getY() - shooter.getPlayer().getLocation().getY());
			float pZ = (float) (loc.getBlock().getZ() - shooter.getPlayer().getLocation().getZ());
			int totalPoints = (int) (Math.round((pX + pY + pZ) * target.getWeigth()));
			Logger.debug("shooter: " + shooter.getName() + " total points: " + totalPoints);
			givePoints(shooter, totalPoints);
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
		Archer archer = findArcherByPlayer(shooter);
		if (archer.getBow().equals(ArcherBow.DEFAULT)) {
			archer.setBow(ArcherBow.DOUBLE);
		} else if (archer.getBow().equals(ArcherBow.DOUBLE)) {
			archer.setBow(ArcherBow.TRIPPLE);
		}
	}

	public void destroyMovingTarget(MovingTarget mTarget) {
		Location loc = mTarget.getBlock().getLocation();
		Logger.debug("destroyMovingTarget - Location " + loc);
		movingTargets.remove(mTarget);
		mTarget.getBlock().setType(Material.AIR);
		if (!mTarget.isHit()) {
			this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		}
	}

	public Location getRandomSpawnLocationForGroundEnemy() {
		Area3D area = this.instance.getArea(Constants.MONSTERS_SPAWN_AREA);
		return LocationUtil.getRandomLocationXYZ(world, area);
	}

	public Location getRandomSpawnLocationForVerticalMovingTarget() {
		Area3D area = this.instance.getArea(Constants.FLOATING_AREA);
		return LocationUtil.getRandomLocationXYZ(world, area);
	}

	public Location getRandomSpawnLocationForGroundTarget() {
		Area3D area = this.instance.getArea(Constants.MONSTERS_SPAWN_AREA);
		return LocationUtil.getRandomLocationXYZ(world, area);
	}

	public Location getRandomSpawnLocationForFloatingTarget() {
		Area3D area = this.instance.getArea(Constants.FLOATING_AREA);
		return LocationUtil.getRandomLocationXYZ(world, area);
	}

	public void hitEntity(Entity entity, Player player) {
		Archer archer = this.findArcherByPlayer(player);
		EntityTarget target = findEntityTarget(entity);
		target.setKiller(player);
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
				Logger.debug("zombie was a target");
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
			Logger.debug("zombie was a NOT target");
		}
		return et;
	}

	public EntityTarget findEntityTarget(Entity entity) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for (EntityTarget z : this.livingTargets) {
			if (z.getLivingEntity().equals(entity)) {
				Logger.debug("entity was a target");
				foundTarget = true;
				et = z;
			}
		}
		if (!foundTarget) {
			Logger.debug("entity was a NOT target");
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
				Logger.debug("killEntity: killer not null");
				Player player = et.getKiller();
				this.givePoints(player, et.getKillPoints());
				this.livingTargets.remove(et);
			} else {
				Logger.debug("killEntity: killer null");
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
		Iterator<Archer> it = this.livePlayers.iterator();
		Archer archer = null;

		while (it.hasNext()) {
			archer = it.next();
			Logger.debug("damageArcherArea - archer: " + archer.getPlayer().getName() + " base: " + new Double(archer.getBaseHealth()));
			if (archer.isNear(zl)) {
				break;
			}
		}
		if (archer != null) {
			Logger.debug("damageArcherArea - base: " + new Double(archer.getBaseHealth()));
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

	public boolean shouldExplodeZombie(Location location) {
		Region r = new Region("spawnLocation", new Location(this.world, 457, 3, 1170),
				new Location(this.world, 493, 3, 1171));

		boolean result = false;
		Logger.debug("shouldExplodeZombie - " + location + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
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

			Logger.debug("shootArrows - " + i + " spread " + spread + " pitch " + pitch + " yaw " + yaw + " x " + x + " y " + y
					+ " z " + z + " z_axis " + z_axis);

			Vector vector = new Vector(x, z, y);
			vector.multiply(3);

			Arrow a = player.getWorld().spawn(player_location, Arrow.class);
			// a.setVelocity(vector);

			player.launchProjectile(Arrow.class, vector);
		}
	}

	public void setArena(String value) {
		this.arena = value;
	}

	public GameDelegate getGameDelegate() {
		return this.delegate;
	}
	
	public int getConfigIntValue(String configName) {
		Config config = null;
		for(Config c: this.instance.getConfigs()) {
			if(c.getName().equalsIgnoreCase(configName)){
				config = c;
				break;
			}
		}
		
		if(config != null) {
			return Integer.parseInt(config.getValue());
		} else {
			return 0;
		}
		
	}


}
