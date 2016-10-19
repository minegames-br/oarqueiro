package br.com.minegames.arqueiro.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.Constants;
import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.util.Utils;
import br.com.minegames.logging.Logger;

public class TeleportToArenaCommand implements CommandExecutor {

	private GameController controller;

    public TeleportToArenaCommand(GameController controller) {
		super();
		this.controller= controller;
	}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    	
    	Logger.log(commandSender.getName() + " " + command.getName() + " " + label + " " + args);
    	
        if (!(commandSender instanceof Player)) {
        	Logger.log(commandSender + " - commando somente para players");
            return false;
        }

        Player player = (Player) commandSender;
        Location l = Utils.toLocation(player.getWorld(), controller.getGameInstance().getArea(Constants.ARENA) );
        player.teleport(l);
        
        return true;
    }

}