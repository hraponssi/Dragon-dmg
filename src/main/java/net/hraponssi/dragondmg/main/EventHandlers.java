package net.hraponssi.dragondmg.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class EventHandlers implements Listener {

	Main plugin;
	
	HashMap<String, Double> dragonDamage = new HashMap<>();//name, damage
	
	public EventHandlers(Main main) {
		this.plugin = main;
	}
	
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) { //Dragon death
    	EntityType type = e.getEntityType();
    	if(type.equals(EntityType.ENDER_DRAGON)) {
    		if(e.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
    			double total = 0;
    			for(Entry<String, Double> entry : dragonDamage.entrySet()) {
    				total += entry.getValue();
    			}
    			String[] tsplit = Double.toString(total).split("\\.");
    			if(plugin.announceKiller) plugin.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.killerMsg.replace("%killer%", e.getEntity().getKiller().getName())));
    			plugin.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.dmgListTitle.replace("%totaldmg%", tsplit[0])));
    			boolean done = false;
    			while(!done) { //Sends the player list in order of damage done
    				double highest = -1;
    				ArrayList<String> remove = new ArrayList<>();
    				for(Entry<String, Double> entry : dragonDamage.entrySet()) {
        				double dmg = entry.getValue();
        				if(dmg > highest) {
        					highest = dmg;
        				}
        			}
    				for(Entry<String, Double> entry : dragonDamage.entrySet()) {
        				double dmg = entry.getValue();
        				String[] split = Double.toString(dmg).split("\\.");
        				if(dmg == highest) {
        					plugin.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.dmgListEntry.replace("%player%", entry.getKey()).replace("%dmg%", split[0])));
        					remove.add(entry.getKey());
        				}
        			}
    				for(String r : remove) {
    					dragonDamage.remove(r);
    				}
    				if(dragonDamage.size() <= 0) done = true;
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) { //Track different types of attacks
    	EntityType type = e.getEntityType();
    	if(type.equals(EntityType.ENDER_DRAGON)) {
    		if(e.getDamager().getType().equals(EntityType.PLAYER)) {
    			Player p = (Player) e.getDamager();
    			if(dragonDamage.containsKey(p.getName())) {
    				double num = dragonDamage.get(p.getName());
    				dragonDamage.replace(p.getName(), num+e.getDamage());
    			}else {
    				dragonDamage.put(p.getName(), e.getDamage());				
    			}
    		} else if(e.getDamager().getType().equals(EntityType.ARROW)) {
    			Arrow arrow = (Arrow) e.getDamager();
                ProjectileSource en = arrow.getShooter();
                if(en instanceof Player) {
                	Player p = (Player) en;
                	if(dragonDamage.containsKey(p.getName())) {
        				double num = dragonDamage.get(p.getName());
        				dragonDamage.replace(p.getName(), num+e.getDamage());
        			}else {
        				dragonDamage.put(p.getName(), e.getDamage());				
        			}
                }
    		} else if(e.getDamager().getType().equals(EntityType.TRIDENT)) {
    			Trident trident = (Trident) e.getDamager();
                ProjectileSource en = trident.getShooter();
                if(en instanceof Player) {
                	Player p = (Player) en;
                	if(dragonDamage.containsKey(p.getName())) {
        				double num = dragonDamage.get(p.getName());
        				dragonDamage.replace(p.getName(), num+e.getDamage());
        			}else {
        				dragonDamage.put(p.getName(), e.getDamage());				
        			}
                }
    		}
    	}
    }
	
}
