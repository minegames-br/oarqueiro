package br.com.minegames.arqueiro;

import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.Arena;
import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.core.domain.Game;
import com.thecraftcloud.core.domain.Local;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.core.util.title.TitleUtil;
import com.thecraftcloud.domain.GamePlayer;
import com.thecraftcloud.plugin.TheCraftCloudMiniGameAbstract;
import com.thecraftcloud.plugin.service.ConfigService;
import com.thecraftcloud.plugin.task.LevelUpTask;

import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.ArcherBow;
import br.com.minegames.arqueiro.domain.TheLastArcher;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.listener.BowShootListener;
import br.com.minegames.arqueiro.listener.EntityHitEvent;
import br.com.minegames.arqueiro.listener.ExplodeListener;
import br.com.minegames.arqueiro.listener.PlayerDeath;
import br.com.minegames.arqueiro.listener.PlayerMove;
import br.com.minegames.arqueiro.listener.TargetHitEvent;
import br.com.minegames.arqueiro.service.ArcherService;
import br.com.minegames.arqueiro.service.LocalService;
import br.com.minegames.arqueiro.service.TargetService;
import br.com.minegames.arqueiro.task.DestroyTargetTask;
import br.com.minegames.arqueiro.task.PlaceMovingTargetTask;
import br.com.minegames.arqueiro.task.PlaceTargetTask;
import br.com.minegames.arqueiro.task.SpawnSkeletonTask;
import br.com.minegames.arqueiro.task.SpawnZombieTask;

public class GameController extends TheCraftCloudMiniGameAbstract {

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
	private CopyOnWriteArraySet<MovingTarget> movingTargets = new CopyOnWriteArraySet<MovingTarget>();

	private int explodeZombieThreadID;
	
	protected LocalService localService = new LocalService(this);
	protected TargetService targetService = new TargetService(this);
	protected ArcherService playerService = new ArcherService(this);
	protected ConfigService configService = ConfigService.getInstance();

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.setSpawnRadius(0);
		
		//ao criar, o jogo fica imediatamente esperando jogadores
		this.myCloudCraftGame = new TheLastArcher();

	}

	@Override
	public void init(World _world, Local _lobby) {
		super.init(_world, _lobby);

		// inicializar variaveis de instancia
		this.placeTargetTask = new PlaceTargetTask(this);
		this.placeMovingTargetTask = new PlaceMovingTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.spawnSkeletonTask = new SpawnSkeletonTask(this);
		// this.spawnZombieTask = new SpawnZombieTask(this);
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
		pm.registerEvents(new PlayerDeath(this), this);
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
			Area3D spawnPoint = (Area3D)configService.getGameArenaConfig("arqueiro.player" + loc + ".area");
			archer.setSpawnPoint(spawnPoint);
			Location l = localService.getMiddle(this.world, spawnPoint);
			System.out.println("yaw: " + l.getYaw());
			System.out.println("pitch " + l.getPitch());
			
			if(!(this.arena.getFacing() == null)) {
				if(this.arena.getFacing() == FacingDirection.EAST) {
					l.setYaw(270);
				} 
				//l.setPitch();
			}
			player.teleport( l );
			
			archer.regainHealthToPlayer(archer);

			// Preparar o jogador para a rodada. Dar armaduras, armas, etc...
			archer.setBow(ArcherBow.DEFAULT);

			BossBar bar = playerService.createBossBar();
			archer.addBaseBar(bar);
			bar.addPlayer(player);

			playerService.setupPlayerToStartGame(player);
			loc++;
		}

		playerService.updateScoreBoards();

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
		targetService.destroyTargets();

		// restaurar parede preta
		//createBlackWall();

		// manda os jogadores para o podium
		playerService.teleportPlayersToPodium();

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

	public void addTarget(Target target) {
		targets.add(target);
	}

	public void addMovingTarget(MovingTarget mTarget) {
		movingTargets.add(mTarget);
	}

	public CopyOnWriteArraySet<Target> getTargets() {
		return this.targets;
	}

	public CopyOnWriteArraySet<MovingTarget> getMovingTargets() {
		return this.movingTargets;
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

	public World getWorld() {
		return this.world;
	}


	public void setArena(Arena value) {
		this.arena = value;
	}

	public Integer getConfigIntValue(String name) {
		return (Integer)this.configService.getGameConfigInstance(name);
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

	@Override
	public Integer getStartCountDown() {
		return this.countDown;
	}

	@Override
	public void setStartCountDown() {
		this.countDown = (Integer)this.configService.getGameConfigInstance(Constants.START_COUNTDOWN);
	}

	@Override
	public Local getLobby() {
		return this.lobbyLocal;
	}

	@Override
	public void setLobby() {
		Local l = (Local)this.configService.getGameArenaConfig(Constants.LOBBY_LOCATION);
		this.lobbyLocal = l;
	}

	@Override
	public Integer getMinPlayers() {
		this.minPlayers = (Integer)this.configService.getGameConfigInstance(Constants.MIN_PLAYERS);
		return this.minPlayers;
	}

	@Override
	public Integer getMaxPlayers() {
		this.maxPlayers = (Integer)this.configService.getGameConfigInstance(Constants.MAX_PLAYERS);
		return this.maxPlayers;
	}

	@Override
	public boolean isGameReady() {
		boolean result = true;
		
		if(this.lobby == null) {
			Bukkit.getLogger().info("Lobby is null");
			result = false;
		}
		
		result = result && super.isGameReady();
		
		return result;
	}

	@Override
	public GamePlayer createGamePlayer() {
		Archer archer = new Archer();
		return archer;
	}

}
