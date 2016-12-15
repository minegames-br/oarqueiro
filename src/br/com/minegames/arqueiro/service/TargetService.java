package br.com.minegames.arqueiro.service;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.thecraftcloud.core.domain.Area3D;
import com.thecraftcloud.core.domain.Arena;
import com.thecraftcloud.core.domain.FacingDirection;
import com.thecraftcloud.core.logging.MGLogger;
import com.thecraftcloud.core.util.BlockManipulationUtil;
import com.thecraftcloud.core.util.Utils;
import com.thecraftcloud.minigame.service.ConfigService;
import com.thecraftcloud.minigame.service.PlayerService;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.arqueiro.domain.target.BlockTarget;
import br.com.minegames.arqueiro.domain.target.FastMovingTarget;
import br.com.minegames.arqueiro.domain.target.FloatingBlockTarget;
import br.com.minegames.arqueiro.domain.target.GroundBlockTarget;
import br.com.minegames.arqueiro.domain.target.MovingTarget;
import br.com.minegames.arqueiro.domain.target.Target;
import br.com.minegames.arqueiro.domain.target.WallBlockTarget;

public class TargetService {
	
	private GameController controller;
	private BlockManipulationUtil blockManipulationUtil = new BlockManipulationUtil();
	private LocalService localService;
	private ArcherService archerService;
	private ConfigService configService = ConfigService.getInstance();

	public TargetService(GameController controller) {
		this.controller = controller;
		this.localService = new LocalService(controller);
		this.archerService = new ArcherService(controller);
	}
	
    /**
     * Criar um alvo que fica no chão
     * @return
     */
    public void createGroundTarget(FacingDirection facing) {
    	Location l = localService.getRandomSpawnLocationForGroundTarget();
    	Block block =  null;
    	if(facing == FacingDirection.EAST || facing == FacingDirection.WEST) {
    		block = createTargetInverted(l);
    	} else {
    		block = createTarget(l);
    	}
    	blockManipulationUtil.createNewWool(configService.getWorld(), l.getBlockX(), l.getBlockY()-1, l.getBlockZ(), DyeColor.WHITE );
    	controller.addTarget(new GroundBlockTarget(block));
    }

    /**
     * Criar um alvo que fica fora da parede e longe do chão
     * @return
     */
    public void createFloatingTarget(FacingDirection facing) {
    	Location l = localService.getRandomSpawnLocationForFloatingTarget();
    	Block block =  null;
    	if(facing == FacingDirection.EAST || facing == FacingDirection.WEST) {
    		block = createTargetInverted(l);
    	} else {
    		block = createTarget(l);
    	}
    	controller.addTarget(new FloatingBlockTarget(block));
    }
    
    public Block createTarget(Location l) {
    	l.setYaw(270);
		int x = l.getBlockX();
		int y = l.getBlockY()+3;
		int z = l.getBlockZ();

    	Block block = createNewBlock(configService.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	blockManipulationUtil .createNewWool(configService.getWorld(), x, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x+1, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x-1, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x+1, y, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x-1, y, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y-1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x+1, y-1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x-1, y-1, z, DyeColor.WHITE );
    	return block;
    }
    
    public Block createTargetInverted(Location l) {
    	l.setYaw(270);
		int x = l.getBlockX();
		int y = l.getBlockY()+3;
		int z = l.getBlockZ();

    	Block block = createNewBlock(configService.getWorld(), x, y, z, Material.RED_SANDSTONE);
    	blockManipulationUtil .createNewWool(configService.getWorld(), x, y+1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y+1, z+1, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y+1, z-1, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y, z+1, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y, z-1, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y-1, z, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y-1, z+1, DyeColor.WHITE );
    	blockManipulationUtil.createNewWool(configService.getWorld(), x, y-1, z-1, DyeColor.WHITE );
    	return block;
    }
    
    public Block createNewBlock(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z, 270, 0);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	return block;
    }

    /**
     * Criar um alvo que "cai" do teto ao chão
     * @return
     */
    public void createVerticalMovingTarget() {
    	Location l = localService.getRandomSpawnLocationForFloatingTarget();
    	MovingTarget target = createTarget(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), Material.BEACON );
    	target.setMaxMoves(getMaxMoves());
    	target.setMoves(0);
    	controller.addMovingTarget(target);
    }
    
    public MovingTarget createTarget(World world, double x, double y, double z, Material type) {
    	
    	Location targetLocation = new Location(world, x, y, z);
        Block block = world.getBlockAt(targetLocation);
    	
       	block.setType(type);
       	
       	FastMovingTarget vmTarget = new FastMovingTarget(block);
       	vmTarget.setMaxMoves(getMaxMoves());
       	vmTarget.setMoves(0);
       	vmTarget.setOldBlockType(block.getType());
       	return vmTarget;
    }
    
    /**
     * Mover todos os alvos verticais um bloco para baixo
     */
    public void moveTargets() {
    	Block block;
    	
    	Iterator<MovingTarget> it = this.controller.getMovingTargets().iterator();
    	
    	while(it.hasNext()) {
    		MovingTarget mt = it.next();
    		Material oldType = mt.getOldBlockType();
    		Location l = mt.getBlock().getLocation();
    		l.getBlock().setType(Material.AIR);
    		int y = l.getBlockY() - 1;
			mt.setMoves(mt.getMoves()+1);
    		
    		if( mt.getMoves() >= mt.getMaxMoves() ) {
    			destroyTarget(mt);
    		} else {
        		l.setY(y);
        		block = l.getBlock();
        		block.setType(Material.BEACON);
        		mt.setBlock(block);
    		}
    		
    	}
    }

	public void destroyTarget(MovingTarget mt) {
		this.destroyMovingTarget(mt);
	}
	
	private Integer getMaxMoves() {
		int maxMoves = 0;
		
    	Arena arena = (Arena)configService.getArena();
    	Area3D area = (Area3D)configService.getGameArenaConfig(Constants.FLOATING_AREA);
    	
    	maxMoves = Math.abs( area.getPointA().getY() - (area.getPointB().getY()) ) -4;
    	
    	return maxMoves;
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

	public void destrobyFloatingBlockTarget(BlockTarget bTarget) {
		// restaurar o local do target
		Block block = bTarget.getBlock();
		Location l1 = null;
		Location l2 = null;
		if(configService.getArena().getFacing() == FacingDirection.EAST || configService.getArena().getFacing() == FacingDirection.WEST) {
			l1 = new Location(configService.getWorld(), block.getX() , block.getY() - 1, block.getZ()-1);
			l2 = new Location(configService.getWorld(), block.getX() , block.getY() + 1, block.getZ()+1);
		} else {
			l1 = new Location(configService.getWorld(), block.getX() - 1, block.getY() - 1, block.getZ());
			l2 = new Location(configService.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		}
		blockManipulationUtil.clearBlocks(l1, l2);
	}

	public void destroyWallBlockTarget(BlockTarget bTarget) {
		// restaurar a parte preta
		Block block = bTarget.getBlock();

		Location l1 = new Location(configService.getWorld(), block.getX() - 1, block.getY() - 1, block.getZ());
		Location l2 = new Location(configService.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		blockManipulationUtil.createWoolBlocks(l1, l2, DyeColor.BLACK);
	}

	public void destroyGroundBlockTarget(BlockTarget bTarget) {
		Block block = bTarget.getBlock();
		Location l1 = null;
		Location l2 = null;
		//os alvos são criados dependendo da direção que a arena foi construída. X ou Z mudam no caso de ser EAST / NORT
		if(configService.getArena().getFacing() == FacingDirection.EAST || configService.getArena().getFacing() == FacingDirection.WEST) {
			l1 = new Location(configService.getWorld(), block.getX(), block.getY() - 2, block.getZ()-1);
			l2 = new Location(configService.getWorld(), block.getX(), block.getY() + 1, block.getZ()+2);
		} else {
			l1 = new Location(configService.getWorld(), block.getX() - 1, block.getY() - 2, block.getZ());
			l2 = new Location(configService.getWorld(), block.getX() + 1, block.getY() + 1, block.getZ());
		}
		blockManipulationUtil .clearBlocks(l1, l2);
	}

	public void destroyTargets() {
		CopyOnWriteArraySet<Target> targets = controller.getTargets();
		for (Target target : targets) {
			if (target instanceof BlockTarget) {
				BlockTarget bTarget = (BlockTarget) target;
				this.destroyBlockTarget(bTarget);
			}
		}

		CopyOnWriteArraySet<MovingTarget> movingTargets = controller.getMovingTargets();
		for (MovingTarget mTarget : movingTargets) {
			this.destroyMovingTarget(mTarget);
		}
	}
	
	public void destroyMovingTarget(MovingTarget mTarget) {
		Location loc = mTarget.getBlock().getLocation();
		controller.getMovingTargets().remove(mTarget);
		mTarget.getBlock().setType(Material.AIR);
		if (!mTarget.isHit()) {
			configService.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		}
	}

	public void hitTarget(BlockTarget target, Player shooter) {
		CopyOnWriteArraySet<Target> targets = controller.getTargets();
		targets.remove(target);
		target.hitTarget2(shooter);
		Location loc = target.getBlock().getLocation();
		shooter.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ() - 1, 1.0F, false, false);
		this.destroyBlockTarget(target);
		if (shooter != null) {
			Double points = loc.distance(shooter.getLocation());
			
			MGLogger.info("shooter: " + shooter.getName() + " total points: " + points);
			this.archerService.givePoints(shooter, points.intValue());
		}

	}

	public void hitMovingTarget(MovingTarget mTarget, Player shooter) {
		mTarget.hitTarget2(shooter);
		Utils.shootFirework(shooter.getLocation());
		this.destroyMovingTarget(mTarget);
		if (shooter != null) {
			this.archerService.giveBonus(shooter);
		}
	}

	public Target hasHittenTarget(Block hit) {
    	Iterator<Target> it = controller.getTargets().iterator();

    	Target targetHitten = null;
    	
    	while(it.hasNext()) {
    		Target target = it.next();

            if( target instanceof BlockTarget ) {
            	
            	BlockTarget bTarget = (BlockTarget)target;
            	Location l1 = hit.getLocation();
            	Location l2 = bTarget.getBlock().getLocation();
            	
            	if( l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ() ) {
            		targetHitten = bTarget;
            		break;
            	}
            }
	    }
    	
	    
    	Iterator<MovingTarget> it2 = controller.getMovingTargets().iterator();

    	while(it2.hasNext()) {
    		MovingTarget mTarget = it2.next();

        	Location l1 = hit.getLocation();
        	Location l2 = mTarget.getBlock().getLocation();
        	
        	if( l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ() ) {
        		targetHitten = mTarget;
        		break;
        	}
        }
    	
    	return targetHitten;
	}

	public void hitTarget(Player shooter, Target target) {
		if(target instanceof BlockTarget) {
			BlockTarget bTarget = (BlockTarget)target;
			this.hitTarget(bTarget, shooter);
		} else {
			MovingTarget mTarget = (MovingTarget)target;
		    this.hitMovingTarget(mTarget, shooter);
		}
	}
}
