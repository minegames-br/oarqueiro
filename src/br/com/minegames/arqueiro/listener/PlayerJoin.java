package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.minegames.arqueiro.GameController;
import br.com.minegames.util.Utils;
import br.com.minegames.util.title.TitleUtil;

public class PlayerJoin implements Listener {

    public PlayerJoin(GameController plugin) {
		super();
		this.game = plugin;
	}

	private GameController game;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        event.setJoinMessage(null);
        Bukkit.getConsoleSender().sendMessage(Utils.color("&6PlayerJoin.onJoin"));
        game.sendToLobby(event.getPlayer());
        Player player = (Player)event.getPlayer();
    	TitleUtil.sendTabTitle(player,"Header","Footer");
        
    }

}