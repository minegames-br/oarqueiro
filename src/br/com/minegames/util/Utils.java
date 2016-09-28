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
    
    public static void shootFirework(Player player) {
        Location location = player.getLocation();
    	shootFirework(location);
    }

    public static Firework shootFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
        
        data.addEffects(FireworkEffect.builder().withColor(Color.GREEN).with( FireworkEffect.Type.BALL_LARGE).build());
        data.setPower(2);
        
        firework.setFireworkMeta(data);
    	
    	return firework;
    }
}