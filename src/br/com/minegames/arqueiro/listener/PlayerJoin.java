package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.minegames.arqueiro.Game;
import br.com.minegames.arqueiro.GameState;
import br.com.minegames.util.Utils;

public class PlayerJoin implements Listener {

    public PlayerJoin(Game plugin) {
		super();
		this.game = plugin;
	}

	private Game game;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    	
        event.setJoinMessage(null);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6PlayerJoin.onJoin"));
        game.sendToLobby(event.getPlayer());
        
    }

}