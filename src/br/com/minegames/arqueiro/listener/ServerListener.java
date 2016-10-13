package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import br.com.minegames.arqueiro.GameController;

public class ServerListener implements Listener {

	private GameController game;

    public ServerListener(GameController plugin) {
		super();
		this.game = plugin;
	}

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStarve(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
    
 
    
}
