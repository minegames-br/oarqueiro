package br.com.minegames.arqueiro.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.logging.Logger;

public class TeleportToArenaCommand implements CommandExecutor {

	private GameController game;

    public TeleportToArenaCommand(GameController plugin) {
		super();
		this.game = plugin;
	}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    	
    	Logger.log(commandSender.getName() + " " + command.getName() + " " + label + " " + args);
    	
        if (!(commandSender instanceof Player)) {
        	Logger.log(commandSender + " - commando somente para players");
            return false;
        }

        Player player = (Player) commandSender;
        player.teleport(game.getSpawnArea().getPointA());
        
        return true;
    }

}