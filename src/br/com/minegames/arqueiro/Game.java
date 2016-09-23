package br.com.minegames.arqueiro;

import java.util.List;
import java.util.Vector;

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

import br.com.minegames.arqueiro.command.JoinGameCommand;
import br.com.minegames.arqueiro.command.LeaveGameCommand;
import br.com.minegames.arqueiro.domain.Archer;
import br.com.minegames.arqueiro.domain.Area2D;
import br.com.minegames.arqueiro.domain.Area3D;
import br.com.minegames.arqueiro.domain.EntityTarget;
import br.com.minegames.arqueiro.domain.Target;
import br.com.minegames.arqueiro.listener.BowShootListener;
import br.com.minegames.arqueiro.listener.EntityHitEvent;
import br.com.minegames.arqueiro.listener.PlayerDeath;
import br.com.minegames.arqueiro.listener.PlayerJoin;
import br.com.minegames.arqueiro.listener.PlayerQuit;
import br.com.minegames.arqueiro.listener.ServerListener;
import br.com.minegames.arqueiro.listener.TargetHitEvent;
import br.com.minegames.arqueiro.task.DestroyTargetTask;
import br.com.minegames.arqueiro.task.EndGameTask;
import br.com.minegames.arqueiro.task.PlaceTargetTask;
import br.com.minegames.arqueiro.task.SpawnZombieTask;
import br.com.minegames.arqueiro.task.StartCoundDownTask;
import br.com.minegames.arqueiro.task.StartGameTask;
import br.com.minegames.logging.Logger;
import br.com.minegames.util.BlockManipulationUtil;
import br.com.minegames.util.LocationUtil;
import br.com.minegames.util.Utils;

public class Game extends JavaPlugin {

	private World world;
	private Vector<Archer> players = new Vector<Archer>();
	private GameState state = GameState.WAITING;
	private Runnable placeTargetTask;
	private int placeTargetThreadID;
	private Runnable destroyTargetTask;
	private int destroyTargetThreadID;
	private Runnable spawnZombieTask;
	private int spawnZombieThreadID;
	private Runnable endGameTask;
	private int endGameThreadID;
	private Vector<Target> targets = new Vector<Target>();
	private Vector<EntityTarget> livingTargets = new Vector<EntityTarget>();
	
	private Vector<Location> arenaSpawnPoints = new Vector<Location>();
	
	private Location lobbyLocation;
	private int maxplayers = 2;
	private int minplayers = 1;
	private int maxZombieSpawned = 4;
	private int countDown = 10;
	private Runnable startCountDownTask;
	private Runnable startGameTask;
	private int startCountDownThreadID;
	private int startGameThreadID;
	private Archer winner;
	
	private Area2D spawnArea;
	private Area2D blackWall;
	private Area3D arena;
	private Area3D floatingArena;
	
	@Override
    public void onEnable() {
        Bukkit.setSpawnRadius(0);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6O 'O Arqueiro - onEnable"));
        
        //registrar os Listeners de eventos do servidor e do jogo
        registerListeners();
		
		//inicializar em que mundo o jogador está. Só deve ter um.
		//não deixar mudar dia/noite
		//não deixar fazer spawn de mobs automatico
		//deixar o horário de dia
        this.world = Bukkit.getWorlds().get(0);
        world.setTime(1000);
        world.setSpawnFlags(false, false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");

        getCommand("jogar").setExecutor(new JoinGameCommand(this));
        getCommand("sair").setExecutor(new LeaveGameCommand(this));
        
        init();

	}
	
	private void init() {
		//mudar o state do jogo para esperar jogadores entrarem
		this.state = GameState.WAITING;
		
		//zerar lista de players
		this.players.clear();
		
		//esse target vai ser usado durante o jogo. Dinamicamente vai ser criado. 
		//quando for acertado vai desaparecer e outro será criado e associado
		this.targets.clear();
		this.livingTargets.clear();

        //Remover qualquer entidade que tenha ficado no mapa
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }

        //inicializar variaveis de instancia
    	this.maxZombieSpawned = 4;
		this.placeTargetTask = new PlaceTargetTask(this);
		this.destroyTargetTask = new DestroyTargetTask(this);
		this.endGameTask = new EndGameTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		this.startCountDownTask = new StartCoundDownTask(this);
		this.startGameTask = new StartGameTask(this);
		this.spawnZombieTask = new SpawnZombieTask(this);
		
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
		this.arenaSpawnPoints.add(new Location(this.getWorld(), 460, a1.getY(), a1.getZ() ) );
		this.arenaSpawnPoints.add(new Location(this.getWorld(), 470, a1.getY(), a1.getZ() ) );
		this.arenaSpawnPoints.add(new Location(this.getWorld(), 480, a1.getY(), a1.getZ() ) );
		this.arenaSpawnPoints.add(new Location(this.getWorld(), 490, a1.getY(), a1.getZ() ) );
		
		this.lobbyLocation = new Location(this.getWorld(), 500, 4, 1165);
		this.countDown = 10;
		this.winner = null;

		//re-criar a parede preta no fundo da arena em que aparecem os targets
		createBlackWall();

        //Agendar as threads que vão detectar se o jogo pode comecar
        BukkitScheduler scheduler = getServer().getScheduler();
        this.startGameThreadID    = scheduler.scheduleSyncRepeatingTask(this, this.startGameTask, 0L, 20L);
        this.startCountDownThreadID    = scheduler.scheduleSyncRepeatingTask(this, this.startCountDownTask, 0L, 25L);
	}

    @Override
    public void onDisable() {
    	if(this.isStarted()) {
    		this.state = GameState.SHUTDOWN;
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
     * Quando esse método rodar, já teremos a quantidade máxima de jogadores na arena
     * ou então a quantidade mínima e o tempo de espera terminou.
     */
    public void startGameEngine() {
        this.state = GameState.RUNNING;

        Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.startGameEngine"));
        
        //Iniciar threads do jogo
        BukkitScheduler scheduler = getServer().getScheduler();
        this.placeTargetThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.placeTargetTask, 0L, 100L);
        this.destroyTargetThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.destroyTargetTask, 0L, 100L);
        this.endGameThreadID      = scheduler.scheduleSyncRepeatingTask(this, this.endGameTask, 0L, 50L);
        this.spawnZombieThreadID  = scheduler.scheduleSyncRepeatingTask(this, this.spawnZombieTask, 0L, 150L);
        
        //Terminar threads de preparacao do jogo
        scheduler.cancelTask(startCountDownThreadID);
        scheduler.cancelTask(startGameThreadID);
        
        int loc = 0;
        for(Archer archer: players) {
        	Player player = archer.getPlayer();
        	player.teleport(this.arenaSpawnPoints.get(loc));

        	//Preparar o jogador para a rodada. Dar armaduras, armas, etc...
        	setupPlayerToStartGame(player);
        	loc++;
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
        
        inventory.addItem(bow);
        inventory.addItem(arrow);
	}

	/**
     * Quando esse método executar, o jogo terá terminado com um vencedor
     * e/ou o tempo terá acabado.  
     */
	public void endGame() {
		
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6Game.endGame"));

        //Terminar threads do jogo
		Bukkit.getScheduler().cancelTask(this.placeTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.destroyTargetThreadID);
		Bukkit.getScheduler().cancelTask(this.endGameThreadID);
		Bukkit.getScheduler().cancelTask(this.spawnZombieThreadID);
		
		//TODO o que vai acontecer com os jogadores quando acabar o jogo?
		//por enquanto vou tirá-los da arena e zerar os inventarios e recriar a parede preta
        for(Archer archer: players) {
        	Player player = archer.getPlayer();
			player.getInventory().clear();
        	player.sendMessage("Você fez " + archer.getPoint() + " pontos." );
		}
        
		//destroyTargets()
        destroyTargets();
        
		//restaurar parede preta
		createBlackWall();
		
		//mandar os jogadores de volta para o lobby
		teleportPlayersBackToLobby();

		if(!this.state.equals(GameState.SHUTDOWN)) {
			//limpar a arena e reiniciar o plugin
			init();
		}
		
	}

	private void destroyTargets() {
		for(Target target: targets) {
			target.destroy();
		}
	}

	private void teleportPlayersBackToLobby() {
		//TODO pegar o location do lobby numa configuracao
        for(Archer archer: players) {
        	Player player = archer.getPlayer();
        	player.teleport(lobbyLocation);
		}
	}

	//TODO recuperar a parede de alguma configuração
	private void createBlackWall() {
		BlockManipulationUtil.createWoolBlocks(this.blackWall.getPointA(), this.blackWall.getPointB(), DyeColor.BLACK);
	}
	
    public GameState getGameState() {
    	return this.state;
    }
    
    public void addPlayer(Player player) {
    	Archer archer = null;
    	if(findArcherByPlayer(player) == null) {
			archer = new Archer();
	    	archer.setPlayer(player);
	    	players.add(archer);
	        player.sendMessage(Utils.color("&aBem vindo, Arqueiro!"));
    	} else {
    		Logger.log("Jogador já está na lista");
    	}
    }
    
    public void removePlayer(Player player) {
    	Archer archer = findArcherByPlayer(player);
    	players.remove(archer);
    	if(players.size() == 0) {
    		this.setGameState(GameState.GAMEOVER);
    		this.endGame();
    	}
    }

	public List<Archer> getPlayers() {
		return this.players;
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
		for(Archer archer: players) {
			if(archer.getPlayer().equals(player)) {
				return archer;
			}
		}
		return null;
	}

	public void startCoundDown() {
		this.state = GameState.STARTING;
	}

	public boolean isStarting() {
		return this.state.equals(GameState.STARTING);
	}

	public boolean isStarted() {
		return this.state.equals(GameState.RUNNING);
	}

	public boolean isWaitingPlayers() {
		return this.state.equals(GameState.WAITING) || this.state.equals(GameState.STARTING);	
	}

	public void proceedCountdown() {
		if(this.countDown != 0) {
			this.countDown --;
			Bukkit.broadcastMessage(Utils.color("&6O jogo vai começar em " + this.countDown + " ..."));
		} else {
	        Bukkit.getScheduler().cancelTask(startCountDownThreadID);
		}
	}

	public void givePoints(Player player, int hitPoints) {
		Archer archer = findArcherByPlayer(player);
		archer.addPoints(hitPoints);
	}

	public Vector<Target> getTargets() {
		synchronized (targets) {
			return this.targets;
		}
	}
	
	public Vector<EntityTarget> getLivingTargets() {
		synchronized (livingTargets) {
			return this.livingTargets;
		}
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
		Player killer = dead.getKiller();
		String killername = killer.getDisplayName();
		String deadname = dead.getDisplayName();
		Bukkit.broadcastMessage(ChatColor.GOLD + " " + deadname + " " + ChatColor.GREEN + "was killed by" + ChatColor.GOLD + " " + killername);

		dead.setHealth(20); // Do not show the respawn screen
        dead.getInventory().clear();
        if( this.isStarted() ) {
        	this.removePlayer(dead);
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
		EntityTarget et = findEntityTargetByZombie(zombie);
		if(et != null) {
			Player player = et.getKiller();
			this.givePoints(player, et.getKillPoints());
			this.livingTargets.remove(et);
		}
	}

	public void setGameState(GameState state) {
		this.state = state;
	}

}
