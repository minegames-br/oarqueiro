package br.com.minegames.arqueiro.listener;

import org.bukkit.event.Listener;

import br.com.minegames.arqueiro.GameController;

public class ZombieDropListener  implements Listener {

    private GameController game;

    public ZombieDropListener(GameController plugin) {
        this.game = plugin;
    }

}
