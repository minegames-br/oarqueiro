package br.com.minegames.arqueiro;

import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.core.domain.Local;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.core.util.title.TitleUtil;
import com.thecraftcloud.minigame.TheCraftCloudMiniGameAbstract;
import com.thecraftcloud.minigame.domain.GamePlayer;
import com.thecraftcloud.minigame.domain.MyCloudCraftGame;
import com.thecraftcloud.minigame.service.ConfigService;
import com.thecraftcloud.minigame.task.LevelUpTask;

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

	protected LocalService localService = new LocalService(this);
	protected TargetService targetService = new TargetService(this);
	protected ArcherService playerService = new ArcherService(this);
	protected ConfigService configService = ConfigService.getInstance();

	@Override
	public void onEnable() {
		super.onEnable();

		Bukkit.setSpawnRadius(0);
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
	}

	@Override
	public void onDisable() {
		if (this.configService.getMyCloudCraftGame() != null && this.configService.getMyCloudCraftGame().isStarted()) {
			this.configService.getMyCloudCraftGame().shutDown();
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

		Bukkit.getConsoleSender().sendMessage(Utils.color("&6[START GAME ENGINE]"));

		// registrar os Listeners de eventos do servidor e do jogo
		this.registerListeners();

		MGLogger.info("Game.startGameEngine");

		// preparar Score Board
		int loc = 1;
		for (GamePlayer gp : livePlayers) {
			Archer archer = (Archer) gp;
			Player player = archer.getPlayer();
			// this.world = player.getWorld();
			Local spawnPoint = (Local) configService.getGameArenaConfig("arqueiro.player" + loc + ".spawn");
			archer.setSpawnPoint(spawnPoint);
			Location l = locationUtil.toLocation(this.configService.getWorld(), spawnPoint);
			Bukkit.getConsoleSender().sendMessage("PLAYER: " + player.getName() + " SPAWN POINT: ");
			Bukkit.getConsoleSender().sendMessage("x: " + l.getX());
			Bukkit.getConsoleSender().sendMessage("y: " + l.getY());
			Bukkit.getConsoleSender().sendMessage("Z: " + l.getZ());
			Bukkit.getConsoleSender().sendMessage("yaw: " + l.getYaw());
			Bukkit.getConsoleSender().sendMessage("pitch " + l.getPitch());

			if (!(this.configService.getArena().getFacing() == null)) {
				if (this.configService.getArena().getFacing() == FacingDirection.EAST) {
					l.setYaw(270);
				} else if (this.configService.getArena().getFacing() == FacingDirection.NORTH) {
					l.setYaw(180);
				}
			}
			Bukkit.getConsoleSender().sendMessage("player world: " + player.getWorld().getName());

			Area3D area = (Area3D) configService.getGameArenaConfig("arqueiro.player" + loc + ".area");
			archer.setArea(area);
			player.teleport(l);

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
		this.destroyTargetThreadID = scheduler.scheduleSyncRepeatingTask(this, this.destroyTargetTask, 0L, 20L);
		//this.spawnZombieThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnZombieTask, 0L, 50L);
		//this.spawnSkeletonThreadID = scheduler.scheduleSyncRepeatingTask(this, this.spawnSkeletonTask, 0L, 50L);
	}

	public boolean shouldEndGame() {
		// Terminar o jogo após o 10 Nível
		if (this.configService.getMyCloudCraftGame().getLevel().getLevel() > 10
				&& this.configService.getMyCloudCraftGame().isStarted()) {
			Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - Time is Over"));
			return true;
		}

		// Terminar o jogo caso não tenha mais jogadores
		if (this.getLivePlayers().size() == 0 && this.configService.getMyCloudCraftGame().isStarted()) {
			Bukkit.getConsoleSender().sendMessage(Utils.color("&6EndGameTask - No more players"));
			return true;
		}
		
		/*
		if (this.getGameDuration() > this.getConfigService().getGameDurationInSeconds()) {
			return true;
		}*/
		return false;
	}

	/**
	 * Quando esse método executar, o jogo terá terminado com um vencedor e/ou o
	 * tempo terá acabado.
	 */
	@Override
	public void endGame() {
		super.endGame();
		if (this.configService.getMyCloudCraftGame().isStarted()) {
			this.configService.getMyCloudCraftGame().endGame();
		}
		MGLogger.info("Game.endGame");

		// Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.placeMovingTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnSkeletonThreadID);
		// Bukkit.getScheduler().cancelTask(this.explodeZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.levelUpTaskID);

		// TODO o que vai acontecer com os jogadores quando acabar o jogo?
		// por enquanto vou tirá-los da arena e zerar os inventarios e recriar a
		// parede preta
		for (GamePlayer gp : livePlayers) {
			Archer archer = (Archer) gp;
			Player player = archer.getPlayer();
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			player.sendMessage("Você fez " + archer.getPoint() + " pontos.");
			archer.regainHealthToPlayer(archer);
		}

		// destroyTargets()
		targetService.destroyTargets();

		// restaurar parede preta
		// createBlackWall();

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

		if (this.configService.getMyCloudCraftGame().getLevel().getLevel() >= 1) {
			for (GamePlayer gp : this.livePlayers) {
				Archer archer = (Archer) gp;
				TitleUtil.sendTitle(archer.getPlayer(), 1, 70, 10,
						"Nível " + this.configService.getMyCloudCraftGame().getLevel().getLevel(), "");
			}

			// liberar o jogo novamente após 5 segundos
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					configService.getMyCloudCraftGame().levelUp();
				}
			}, 100L);
		} else {
			this.configService.getMyCloudCraftGame().levelUp();
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
		return this.configService.getMyCloudCraftGame().getLevel().getLevel() == 11;
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

	public Integer getConfigIntValue(String name) {
		return (Integer) this.configService.getGameConfigInstance(name);
	}

	public Object getGameEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GamePlayer createGamePlayer() {
		Archer archer = new Archer();
		return archer;
	}

	@Override
	public MyCloudCraftGame createMyCloudCraftGame() {
		return new TheLastArcher();
	}

}
