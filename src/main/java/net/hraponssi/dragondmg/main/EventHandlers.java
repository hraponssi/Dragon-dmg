package net.hraponssi.dragondmg.main;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    HashMap<String, Double> dragonDamage = new HashMap<>(); //name, damage
    HashMap<String, Integer> crystalKills = new HashMap<>(); //name, crystal count

    public EventHandlers(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) { //Dragon death
        EntityType type = e.getEntityType();
        if (type.equals(EntityType.ENDER_DRAGON)) {
            if (e.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
                double total = 0;
                Location loc = e.getEntity().getLocation();
                String cleanedWorldName = loc.getWorld().getName();
                String defaultPrefix = Bukkit.getWorlds().get(0).getName();
                if (plugin.executeFriendlyNames) {
                    // Vanilla Minecraft execute commands don't include the default world name like Spigot does, instead using "overworld", "the_end", etc
                    // Because of this the overworld, nether, and end have special cases:
                    if (cleanedWorldName.equals(defaultPrefix)) {
                        cleanedWorldName = "overworld";
                    } else if (cleanedWorldName.equals(defaultPrefix + "_the_end")) {
                        cleanedWorldName = cleanedWorldName.replace(defaultPrefix + "_", "");
                    } else if (cleanedWorldName.equals(defaultPrefix + "_the_nether")) {
                        cleanedWorldName = cleanedWorldName.replace(defaultPrefix + "_", "");
                    }
                }
                for (Entry<String, Double> entry : dragonDamage.entrySet()) {
                    total += entry.getValue();
                }
                String[] tsplit = Double.toString(total).split("\\.");
                if (plugin.announceKiller) {
                    plugin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.killerMsg.replaceAll("%killer%", e.getEntity().getKiller().getName())));
                }
                if (plugin.killReward) {
                    for (String reward : plugin.killRewards) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                reward.replaceAll("%killer%", e.getEntity().getKiller().getName())
                                        .replaceAll("%world%", cleanedWorldName)
                                        .replaceAll("%coordinates%", loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()));
                    }
                }
                plugin.sendMessage(ChatColor
                        .translateAlternateColorCodes('&', plugin.dmgListTitle.replaceAll("%totaldmg%", tsplit[0]))
                        .replaceAll("%killer%", e.getEntity().getKiller().getName()));
                List<Double> dmgOrder = dragonDamage.values().stream().distinct().collect(Collectors.toList()); //List of all distinct damage values
                dragonDamage.keySet().stream().forEach(name -> crystalKills.putIfAbsent(name, 0));
                Collections.sort(dmgOrder, new Comparator<Double>() { //Sort list biggest to smallest
                    public int compare(Double db1, Double db2) {
                        return db2.compareTo(db1);
                    }
                });
                for (double dmglevel : dmgOrder) { // Iterate over the damage values in order
                    for (Entry<String, Double> entry : dragonDamage.entrySet()) { // Print out players who's damage matches the damage value being processed
                        double dmg = entry.getValue();
                        String cleandmg = Double.toString(dmg).split("\\.")[0];
                        if (dmg == dmglevel) {
                            plugin.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.dmgListEntry.replaceAll("%player%", entry.getKey()).replaceAll("%dmg%", cleandmg)
                                            .replaceAll("%totaldmg%", tsplit[0])
                                            .replaceAll("%crystals%", crystalKills.get(entry.getKey()).toString())
                                            .replaceAll("%killer%", e.getEntity().getKiller().getName())));
                            Random random = new Random();
                            float dmgp = (Float.parseFloat(cleandmg) / Float.parseFloat(tsplit[0])) * 100;
                            // Rewards
                            int boost = 0;
                            if (plugin.crystalRewardBoost) {
                                boost = crystalKills.getOrDefault(entry.getKey(), 0) * plugin.crystalBoostPercent;
                            }
                            if (plugin.xpReward) {
                                Player player = Bukkit.getPlayer(entry.getKey());
                                if (player != null) {
                                    if (plugin.damageXPMultiplier) {
                                        float xp = ((float) plugin.baseXP) * (Math.min(dmgp+boost, 100) / 100f);
                                        // plugin.getLogger().info("agive xp " + player.getName() + " " + xp);
                                        player.giveExp(Math.round(xp));
                                    } else {
                                        player.giveExp(plugin.baseXP);
                                    }
                                }
                            }
                            if (plugin.dmgReward && random.nextInt(100) + 1 <= Math.min(dmgp+boost, 100)) { // if dmg percent equal or more than random 1-100 number
                                for (String reward : plugin.dmgRewards) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            reward.replaceAll("%player%", entry.getKey()).replaceAll("%dmg%", cleandmg)
                                                    .replaceAll("%totaldmg%", tsplit[0])
                                                    .replaceAll("%crystals%", crystalKills.get(entry.getKey()).toString())
                                                    .replaceAll("%killer%", e.getEntity().getKiller().getName()));
                                }
                            }
                        }
                    }
                }
                crystalKills.clear();
                dragonDamage.clear();
            }
        }
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) { //Track different types of attacks
        EntityType type = e.getEntityType();
        if (e.getDamager().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getDamager();
            if (type.equals(EntityType.ENDER_DRAGON)) {
                if (dragonDamage.containsKey(p.getName())) {
                    double num = dragonDamage.get(p.getName());
                    dragonDamage.replace(p.getName(), num + e.getDamage());
                } else {
                    dragonDamage.put(p.getName(), e.getDamage());
                }
            // hacky way to get around END vs ENDER
            } else if (type.name().startsWith("END") && type.name().endsWith("_CRYSTAL")) {
                if (crystalKills.containsKey(p.getName())) {
                    int num = crystalKills.get(p.getName());
                    crystalKills.replace(p.getName(), num + 1);
                } else {
                    crystalKills.put(p.getName(), 1);
                }
            }
        } else if (e.getDamager().getType().equals(EntityType.ARROW)) {
            Arrow arrow = (Arrow) e.getDamager();
            ProjectileSource en = arrow.getShooter();
            if (en instanceof Player) {
                Player p = (Player) en;
                if (type.equals(EntityType.ENDER_DRAGON)) {
                    if (dragonDamage.containsKey(p.getName())) {
                        double num = dragonDamage.get(p.getName());
                        dragonDamage.replace(p.getName(), num + e.getDamage());
                    } else {
                        dragonDamage.put(p.getName(), e.getDamage());
                    }
                } else if (type.name().startsWith("END") && type.name().endsWith("_CRYSTAL")) {
                    if (crystalKills.containsKey(p.getName())) {
                        int num = crystalKills.get(p.getName());
                        crystalKills.replace(p.getName(), num + 1);
                    } else {
                        crystalKills.put(p.getName(), 1);
                    }
                }
            }
        } else if (e.getDamager().getType().equals(EntityType.TRIDENT)) {
            Trident trident = (Trident) e.getDamager();
            ProjectileSource en = trident.getShooter();
            if (en instanceof Player) {
                Player p = (Player) en;
                if (type.equals(EntityType.ENDER_DRAGON)) {
                    if (dragonDamage.containsKey(p.getName())) {
                        double num = dragonDamage.get(p.getName());
                        dragonDamage.replace(p.getName(), num + e.getDamage());
                    } else {
                        dragonDamage.put(p.getName(), e.getDamage());
                    }
                } else if (type.name().startsWith("END") && type.name().endsWith("_CRYSTAL")) {
                    if (crystalKills.containsKey(p.getName())) {
                        int num = crystalKills.get(p.getName());
                        crystalKills.replace(p.getName(), num + 1);
                    } else {
                        crystalKills.put(p.getName(), 1);
                    }
                }
            }
        }
    }
}
