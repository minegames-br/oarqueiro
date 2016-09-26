package br.com.minegames.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class Utils {

    private Utils() {
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static Firework shootFirework(Player player) {
        Location loc = player.getLocation();
        Firework firework = player.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
        
        data.addEffects(FireworkEffect.builder().withColor(Color.GREEN).with( FireworkEffect.Type.BALL_LARGE).build());
        data.setPower(2);
        
        firework.setFireworkMeta(data);
    	
    	return firework;
    }
}