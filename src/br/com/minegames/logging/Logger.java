package br.com.minegames.logging;

import org.bukkit.Bukkit;

import br.com.minegames.core.util.Utils;

public class Logger {

	public static void log(String message) {
		
		Bukkit.getConsoleSender().sendMessage(Utils.color("&6" + message ));
		
	}
	
}
