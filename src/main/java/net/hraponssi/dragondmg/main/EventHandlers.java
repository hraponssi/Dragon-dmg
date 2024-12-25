package net.hraponssi.dragondmg.main;

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
    Tracker tracker;

    public EventHandlers(Main main) {
        this.plugin = main;
        this.tracker = new Tracker(main);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) { //Dragon death
        EntityType type = e.getEntityType();
        if (type.equals(EntityType.ENDER_DRAGON)) {
            if (e.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
            	tracker.handleKill(e.getEntity().getKiller(), e.getEntity());
            }
        }
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) { //Track different types of attacks
        EntityType type = e.getEntityType();
        if (e.getDamager().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getDamager();
            tracker.handleDamage(p, type, e.getDamage());
        } else if (e.getDamager().getType().equals(EntityType.ARROW)) {
            Arrow arrow = (Arrow) e.getDamager();
            ProjectileSource en = arrow.getShooter();
            if (en instanceof Player) {
                Player p = (Player) en;
                tracker.handleDamage(p, type, e.getDamage());
            }
        } else if (e.getDamager().getType().equals(EntityType.TRIDENT)) {
            Trident trident = (Trident) e.getDamager();
            ProjectileSource en = trident.getShooter();
            if (en instanceof Player) {
                Player p = (Player) en;
                tracker.handleDamage(p, type, e.getDamage());
            }
        }
    }
}
