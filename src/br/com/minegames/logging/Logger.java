package br.com.minegames.logging;

import org.bukkit.Bukkit;

import br.com.minegames.util.Utils;

public class Logger {

	public static void log(String message) {
		
		Bukkit.getConsoleSender().sendMessage(Utils.color("&6" + message ));
		
	}
	
}
