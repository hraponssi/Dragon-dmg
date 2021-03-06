package net.hraponssi.dragondmg.main;

import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	boolean announceKiller;
	boolean worldOnlyMsg;
	boolean killReward;
	boolean dmgReward;
	
	String dmgListTitle;
	String dmgListEntry;
	String killerMsg;
	String worldName;
	
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
		announceKiller = getConfig().getBoolean("announcekiller");
		worldOnlyMsg = getConfig().getBoolean("worldonlymsg");
		killReward = getConfig().getBoolean("enablekillerrewards");
		dmgReward = getConfig().getBoolean("enabledmgrewards");
		worldName = getConfig().getString("worldname");
		dmgListTitle = getConfig().getString("dmglisttitle");
		dmgListEntry = getConfig().getString("dmglistentry");
		killerMsg = getConfig().getString("killermsg");
		killRewards = getConfig().getStringList("killrewards");
		dmgRewards = getConfig().getStringList("dmgrewards");
	}
	
	public void sendMessage(String msg) {
		if(worldOnlyMsg) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getWorld().getName().equals(worldName)){
					p.sendMessage(msg);
				}
			}
		}else {
			Bukkit.broadcastMessage(msg);
		}
	}
			
}
