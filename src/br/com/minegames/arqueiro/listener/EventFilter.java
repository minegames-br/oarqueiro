package br.com.minegames.arqueiro.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.minegames.arqueiro.GameController;

public class EventFilter implements Listener {
    private GameController controller;

    public EventFilter(GameController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onAnyEvent(Event event) {
    	if(controller.getTheLastArcher() == null || !controller.getTheLastArcher().isStarted() ) {
    		Bukkit.getConsoleSender().sendMessage("event" + event.getEventName());
    	}
    }

}
