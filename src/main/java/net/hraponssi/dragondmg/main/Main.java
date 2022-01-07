package net.hraponssi.dragondmg.main;

import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	boolean announcekiller;
	
	String dmgListTitle;
	String dmgListEntry;
	String killerMsg;
	
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
		announcekiller = getConfig().getBoolean("announcekiller");
		dmgListTitle = getConfig().getString("dmglisttitle");
		dmgListEntry = getConfig().getString("dmglistentry");
		killerMsg = getConfig().getString("killermsg");
	}
			
}
