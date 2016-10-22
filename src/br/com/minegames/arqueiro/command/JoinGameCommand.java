package br.com.minegames.arqueiro.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.core.logging.Logger;

public class JoinGameCommand implements CommandExecutor {

	private GameController game;

    public JoinGameCommand(GameController plugin) {
		super();
		this.game = plugin;
	}

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    	
    	Logger.debug("JoinGameCommand onCommand - " + commandSender.getName() + " " + command.getName() + " " + label + " " + args);
    	
        if (!(commandSender instanceof Player)) {
        	Logger.debug(commandSender + " - commando somente para players");
            return false;
        }
        
        Player player = (Player) commandSender;
        if(game.getLivePlayers() != null && game.getLivePlayers().size() > 0) {
        	if(args != null || args.length > 0) {
        		player.sendMessage("Uma arena está ativa. Vou te mandar pra lá.");
        	}
            game.addPlayer(player);
        } else {
	        
	        if(args == null || args.length != 1) {
	        	player.sendMessage("/jogar <arena> exemplo: /jogar arena1 ou /jogar arena3");
	        	return false;
	        } else {
	        	game.setArena(args[0]);
	            game.addPlayer(player);
	        }

        }
        
        return true;
    }

}