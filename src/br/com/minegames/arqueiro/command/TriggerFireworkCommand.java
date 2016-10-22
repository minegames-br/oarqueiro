package br.com.minegames.arqueiro.command;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.logging.Logger;
import br.com.minegames.core.util.Utils;
import br.com.minegames.core.worldedit.WorldEditWrapper;

public class TriggerFireworkCommand implements CommandExecutor {

	private GameController game;

    public TriggerFireworkCommand(GameController plugin) {
		super();
		this.game = plugin;
	}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    	
    	Logger.debug(commandSender.getName() + " " + command.getName() + " " + label + " " + args);
    	
        if (!(commandSender instanceof Player)) {
        	Logger.debug(commandSender + " - commando somente para players");
            return false;
        }

        Player player = (Player) commandSender;
        
        File file = new File("c:/Temp/arena2_fire.schematic");
        WorldEditWrapper.loadSchematic(player.getWorld(), file, player.getLocation());
        
        
        Utils.shootFirework(player);
        
        return true;
    }

}