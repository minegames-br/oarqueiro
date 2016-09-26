package br.com.minegames.arqueiro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import br.com.minegames.arqueiro.command.JoinGameCommand;
import br.com.minegames.arqueiro.command.LeaveGameCommand;
import br.com.minegames.arqueiro.command.StartGameCommand;
import br.com.minegames.arqueiro.command.TeleportToArenaCommand;
import br.com.minegames.arqueiro.command.TriggerFireworkCommand;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.Area2D;
import br.com.minegames.arqueiro.domain.Area3D;
import br.com.minegames.arqueiro.domain.Game;
import br.com.minegames.arqueiro.domain.target.EntityTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.ZombieTarget;
import br.com.minegames.arqueiro.listener.BowShootListener;
import br.com.minegames.arqueiro.listener.EntityHitEvent;
import br.com.minegames.arqueiro.listener.PlayerDeath;
import br.com.minegames.arqueiro.listener.PlayerJoin;
import br.com.minegames.arqueiro.listener.PlayerQuit;
import br.com.minegames.arqueiro.listener.ServerListener;
import br.com.minegames.arqueiro.listener.TargetHitEvent;
import br.com.minegames.arqueiro.task.DestroyTargetTask;
import br.com.minegames.arqueiro.task.EndGameTask;
import br.com.minegames.arqueiro.task.ExplodeZombieTask;
import br.com.minegames.arqueiro.task.LevelUpTask;
import br.com.minegames.arqueiro.task.PlaceTargetTask;
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
	private Runnable destroyTargetTask;
	private int destroyTargetThreadID;
	private Runnable spawnZombieTask;
	private int spawnZombieThreadID;
	private Runnable endGameTask;
	private int endGameThreadID;
	private Runnable levelUpTask;
	private HashMap<Location, Material> materialsToRestore = new HashMap<Location, Material>();
	private int levelUpThreadID;
	private CopyOnWriteArraySet<Target> targets = new CopyOnWriteArraySet<Target>();
	private CopyOnWriteArraySet<EntityTarget> livingTargets = new CopyOnWriteArraySet<EntityTarget>();
	
	private CopyOnWriteArraySet<Area2D> arenaSpawnPoints = new CopyOnWriteArraySet<Area2D>();
	
	private Location lobbyLocation;
	private int maxplayers = 4;
	private int minplayers = 1;
	private int maxZombieSpawned = 5;
	private int maxTarget = 3;
	private int countDown = 20;
	private long gameStartTime;
	private Runnable startCountDownTask;
	private int startCountDownThreadID;
	private Runnable startGameTask;
	private int startGameThreadID;

	private Runnable explodeZombieTask;
	private int explodeZombieThreadID;
	private Archer winner;
	private CopyOnWriteArraySet<String> playerNames = new CopyOnWriteArraySet<String>();
	
	private Area2D spawnArea;
	private Area2D blackWall;
	private Area3D arena;
	private Area3D floatingArena;

	private Scoreboard scoreboard;
	
	@Override
    public void onEnable() {
        Bukkit.setSpawnRadius(0);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6O 'O Arqueiro - onEnable"));
        
        //registrar os Listeners de eventos do servidor e do jogo
        registerListeners();
		
		//inicializar em que mundo o jogador est�. S� deve ter um.
		//n�o deixar mudar dia/noite
		//n�o deixar fazer spawn de mobs automatico
		//deixar o hor�rio de dia
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
		
		this.arenaSpawnPoints.clear();
		this.arenaSpawnPoints.add(new Area2D(new Location(this.getWorld(), 457, a1.getY(), a1.getZ() ), new Location(this.getWorld(), 463, a1.getY(), a1.getZ() )));
		this.arenaSpawnPoints.add(new Area2D(new Location(this.getWorld(), 467, a1.getY(), a1.getZ() ), new Location(this.getWorld(), 473, a1.getY(), a1.getZ() ) ));
		this.arenaSpawnPoints.add(new Area2D(new Location(this.getWorld(), 477, a1.getY(), a1.getZ() ), new Location(this.getWorld(), 483, a1.getY(), a1.getZ() ) ));
		this.arenaSpawnPoints.add(new Area2D(new Location(this.getWorld(), 487, a1.getY(), a1.getZ() ), new Location(this.getWorld(), 493, a1.getY(), a1.getZ() ) ));
		
		this.lobbyLocation = new Location(this.getWorld(), 530, 4, 1210);
		
		for(int i = arena.getPointA().getBlockX(); i < arena.getPointB().getBlockX(); i++ ) {
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
		
		//zerar lista de players
		this.playerList.clear();
		this.livePlayers.clear();
		this.playerNames.clear();
		
		//esse target vai ser usado durante o jogo. Dinamicamente vai ser criado. 
		//quando for acertado vai desaparecer e outro ser� criado e associado
		this.targets.clear();
		this.livingTargets.clear();

        //Remover qualquer entidade que tenha ficado no mapa
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }

        //inicializar variaveis de instancia
    	this.maxZombieSpawned = 5;
		this.placeTargetTask = new PlaceTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.endGameTask = new EndGameTask(this);
		this.levelUpTask = new LevelUpTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.startCountDownTask = new StartCoundDownTask(this);
		this.startGameTask = new StartGameTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.explodeZombieTask = new ExplodeZombieTask(this);
		
		this.countDown = 10;
		this.winner = null;

		//re-criar a parede preta no fundo da arena em que aparecem os targets
		createBlackWall();
		
		//re-criar prote��o do player (cerca)
		Iterator<Location> it = materialsToRestore.keySet().iterator();
		while(it.hasNext()) {
			Location l = it.next();
			Material m = materialsToRestore.get(l);
			world.getBlockAt(l).setType(m);
		}

        //Agendar as threads que v�o detectar se o jogo pode comecar
        BukkitScheduler scheduler = getServer().getScheduler();
        this.startGameThreadID    = scheduler.scheduleSyncRepeatingTask(this, this.startGameTask, 0L, 20L);
        this.startCountDownThreadID    = scheduler.scheduleSyncRepeatingTask(this, this.startCountDownTask, 0L, 25L);

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = this.scoreboard.registerNewObjective(Utils.color("&6Placar"), "placar");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

	}

    @Override
    public void onDisable() {
    	if(this.game.isStarted()) {
    		this.game.shutDown();
    		this.endGame();
    	}
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(this), this );
        pm.registerEvents(new PlayerQuit(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new TargetHitEvent(this), this);
        pm.registerEvents(new EntityHitEvent(this), this);
        pm.registerEvents(new ServerListener(this), this);
        pm.registerEvents(new BowShootListener(this), this);
    }
    
    /*
     * Quando esse m�todo rodar, j� teremos a quantidade m�xima de jogadores na arena
     * ou ent�o a quantidade m�nima e o tempo de espera terminou.
     */
    public void startGameEngine() {
        this.game.start(); 
        
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.startGameEngine"));
        
        //preparar Score Board
        int loc = 0;
        for(Archer archer: livePlayers) {
        	Player player = archer.getPlayer();
        	Area2D spawnPoint = ((Area2D)this.arenaSpawnPoints.toArray()[loc]);
        	archer.setSpawnPoint(spawnPoint);
        	player.teleport(spawnPoint.getPointA());
        	
        	//Preparar o jogador para a rodada. Dar armaduras, armas, etc...
        	setupPlayerToStartGame(player);
        	loc++;
        }
        
        updateScoreBoards();

        BukkitScheduler scheduler = getServer().getScheduler();

        //Terminar threads de preparacao do jogo
        scheduler.cancelTask(startCountDownThreadID);
        scheduler.cancelTask(startGameThreadID);
        
        //Iniciar threads do jogo
        this.placeTargetThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.placeTargetTask, 0L, 50L);
        this.destroyTargetThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.destroyTargetTask, 0L, 100L);
        this.endGameThreadID      = scheduler.scheduleSyncRepeatingTask(this, this.endGameTask, 0L, 50L);
        this.spawnZombieThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.spawnZombieTask, 0L, 50L);
        this.levelUpThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.levelUpTask, 0L, 100L);
        this.explodeZombieThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.explodeZombieTask, 0L, 20L);
        
    }

    private void updateScoreBoards() {

    	int index = 0;
        for(Archer archer: this.livePlayers) {
        	Player player = archer.getPlayer();
        	String name = (String)playerNames.toArray()[index];
        	this.scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(name).setScore( archer.getPoint() );
        	index ++;
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
        
        inventory.addItem(bow);
        inventory.addItem(arrow);
        
        player.setScoreboard(this.scoreboard);
        
	}

	/**
     * Quando esse m�todo executar, o jogo ter� terminado com um vencedor
     * e/ou o tempo ter� acabado.  
     */
	public void endGame() {
		if(this.game.isStarted()) {
			this.game.endGame();
		}
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.endGame"));

        //Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.endGameThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		Bukkit.getScheduler().cancelTask(this.levelUpThreadID);
		Bukkit.getScheduler().cancelTask(this.explodeZombieThreadID);
		
		//TODO o que vai acontecer com os jogadores quando acabar o jogo?
		//por enquanto vou tir�-los da arena e zerar os inventarios e recriar a parede preta
        for(Archer archer: livePlayers) {
        	Player player = archer.getPlayer();
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        	player.sendMessage("Voc� fez " + archer.getPoint() + " pontos." );
		}
        
		//destroyTargets()
        destroyTargets();
        
		//restaurar parede preta
		createBlackWall();
		
		//mandar os jogadores de volta para o lobby
		teleportPlayersBackToLobby();

		//limpar inventario do jogador
		clearPlayersInventory();
		
		if(!this.game.isShuttingDown()) {
			//limpar a arena e reiniciar o plugin
			init();
		}
		
	}
	
    private void clearPlayersInventory() {
        for(Archer archer: livePlayers) {
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
		this.game.levelUp();
		for(Archer archer: this.livePlayers) {
			TitleUtil.sendTitle(archer.getPlayer(), 1, 20, 10, "N�vel " + this.game.getLevel().getLevel(), "");
		}
	}

	private void destroyTargets() {
		for(Target target: targets) {
			target.destroy();
		}
	}

	private void teleportPlayersBackToLobby() {
		//TODO pegar o location do lobby numa configuracao
        for(Archer archer: livePlayers) {
        	Player player = archer.getPlayer();
        	player.teleport(lobbyLocation);
		}
	}

	//TODO recuperar a parede de alguma configura��o
	private void createBlackWall() {
		BlockManipulationUtil.createWoolBlocks(this.blackWall.getPointA(), this.blackWall.getPointB(), DyeColor.BLACK);
	}

    public void addPlayer(Player player) {
    	Archer archer = null;
    	if(findArcherByPlayer(player) == null) {
			archer = new Archer();
	    	archer.setPlayer(player);
	    	playerList.add(archer);
	    	livePlayers.add(archer);
	        player.sendMessage(Utils.color("&aBem vindo, Arqueiro!"));
	        playerNames.add(player.getName());
    	} else {
    		Logger.log("Jogador j� est� na lista");
    	}
    }
    
    public void removeLivePlayer(Player player) {
    	Archer archer = findArcherByPlayer(player);

    	player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    	player.getInventory().clear();
    	player.teleport(lobbyLocation);
    	livePlayers.remove(archer);
    	
    	if(livePlayers.size() == 0) {
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
		return countDown ;
	}

	public Archer getWinner() {
		return winner;
	}

	public void setWinner(Archer winner) {
		this.winner = winner;
	}
	
	public Archer findArcherByPlayer(Player player) {
		for(Archer archer: playerList) {
			if(archer.getPlayer().equals(player)) {
				return archer;
			}
		}
		return null;
	}

	public void startCoundDown() {
		this.game.startCountDown();
	}

	public void proceedCountdown() {
		if(this.countDown != 0) {
			this.countDown --;
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
	
	public CopyOnWriteArraySet<EntityTarget> getLivingTargets() {
		return this.livingTargets;
	}
	
	public Area2D getSpawnArea() {
		return spawnArea;
	}

	public void setSpawnArea(Area2D spawnArea) {
		this.spawnArea = spawnArea;
	}

	public void hitTarget(Target target, Player shooter) {
		targets.remove(target);
		target.hitTarget2(shooter);
	}

	public void hitEntityTarget(Target target, Player shooter) {
		target.hitTarget2(shooter);
	}

	public void kill2EntityTarget(EntityTarget target, Player shooter) {
		livingTargets.remove(target);
		this.givePoints(shooter, target.getKillPoints());
	}

	public void sendToLobby(Player player) {
		player.teleport(lobbyLocation);
	}
	
	public Location getRandomSpawnLocationForGroundEnemy() {
		return LocationUtil.getRandomLocationXZ(world, this.spawnArea);
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
       	int damage = archer.getCurrentArrowDamage();
        EntityTarget targetZombie = findEntityTargetByZombie(entity);
        targetZombie.setKiller(player);
        entity.damage(damage);
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
        if( this.game.isStarted() ) {
        	this.removeLivePlayer(dead);
        }
        
        this.sendToLobby(dead);
	 }

	public EntityTarget findEntityTargetByZombie(Zombie zombie) {
		boolean foundTarget = false;
		EntityTarget et = null;
		for(EntityTarget z: this.livingTargets) {
			if(z.getLivingEntity().equals(zombie)) {
				Logger.log("zombie was a target");
		    	foundTarget = true;
		    	et = z;
			}
		}
		if(!foundTarget) {
			Logger.log("zombie was a NOT target");
		}
		return et;
    }
	
	public void killZombie(Zombie zombie) {
		ZombieTarget et = (ZombieTarget)findEntityTargetByZombie(zombie);
		Location loc = zombie.getLocation();
		if(et != null) {
			if(et.getKiller() != null) {
				Player player = et.getKiller();
				this.givePoints(player, et.getKillPoints());
				this.livingTargets.remove(et);
			} else {
				if(damageArcherArea(zombie)) {
					zombie.damage(zombie.getMaxHealth());
				    this.world.createExplosion(loc.getX(), loc.getY(), loc.getZ()-1, 2.0F, false, false);
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
		while(it.hasNext()) {
			archer = it.next();
			if(archer.isNear(zl)) {
				break;
			}
		}
		if(archer != null) {
			if(archer.getBaseHealth() <= 0) {
				return false;
			}else {
				archer.damageBase();
				Logger.log("base: " + archer.getBaseHealth());
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
		return this.game.getLevel().getLevel() == 10;
	}

	public int getMaxTarget() {
		return this.maxTarget;
	}

	public boolean shouldExplodeZombie(Location location) {
		Region r = new Region("spawnLocation", new Location(this.world, 457, 3, 1170), new Location(this.world, 493, 3, 1171));

		boolean result = false;
		Logger.log(location + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() );
		if(location.getBlockX() >= 457 && location.getBlockX() <= 493) {
			if(location.getBlockZ() >= 1170 && location.getBlockZ() <= 1171) {
				result = true;
			}
		}
		
		
		
		return result;
	}

}