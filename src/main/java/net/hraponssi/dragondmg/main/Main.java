package net.hraponssi.dragondmg.main;

import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    boolean announceKiller;
    boolean worldOnlyMsg;
    boolean xpReward;
    boolean damageXPMultiplier;
    boolean crystalRewardBoost;
    boolean killReward;
    boolean dmgReward;
    boolean executeFriendlyNames;

    String dmgListTitle;
    String dmgListEntry;
    String killerMsg;
    String worldName;

    int baseXP;
    int crystalBoostPercent;

    List<String> killRewards;
    List<String> dmgRewards;

    @Override
    public void onEnable() {
        int pluginId = 13878;
        Metrics metrics = new Metrics(this, pluginId);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventHandlers(this), this);
        getCommand("dragondmg").setExecutor(new Commands(this));
        saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();
        announceKiller = config.getBoolean("announcekiller");
        worldOnlyMsg = config.getBoolean("worldonlymsg");
        xpReward = config.getBoolean("enablexprewards");
        damageXPMultiplier = config.getBoolean("damagexpmultiplier");
        baseXP = config.getInt("basexp");
        crystalRewardBoost = config.getBoolean("crystalrewardboost");
        crystalBoostPercent = config.getInt("crystalboostpercent");
        killReward = config.getBoolean("enablekillerrewards");
        dmgReward = config.getBoolean("enabledmgrewards");
        worldName = config.getString("worldname");
        dmgListTitle = config.getString("dmglisttitle");
        dmgListEntry = config.getString("dmglistentry");
        killerMsg = config.getString("killermsg");
        killRewards = config.getStringList("killrewards");
        dmgRewards = config.getStringList("dmgrewards");
        executeFriendlyNames = config.getBoolean("executefriendlyworldnames");
    }

    public void sendMessage(String msg) {
        if (worldOnlyMsg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().getName().equals(worldName)) {
                    p.sendMessage(msg);
                }
            }
        } else {
            Bukkit.broadcastMessage(msg);
        }
    }

}
