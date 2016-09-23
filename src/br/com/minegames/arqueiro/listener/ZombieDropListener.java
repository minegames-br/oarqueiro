package br.com.minegames.arqueiro.listener;

import org.bukkit.event.Listener;

import br.com.minegames.arqueiro.Game;

public class ZombieDropListener  implements Listener {

    private Game game;

    public ZombieDropListener(Game plugin) {
        this.game = plugin;
    }

}
