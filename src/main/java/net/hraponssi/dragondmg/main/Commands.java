package net.hraponssi.dragondmg.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

	Main plugin;
	
	public Commands(Main main) {
		this.plugin = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		String scmd = cmd.getName();
		if(scmd.equalsIgnoreCase("dragondmg")){
			if(args.length > 0) {
				switch(args[0].toLowerCase()) {
				case "reload":
					plugin.loadConfig();
					sender.sendMessage(ChatColor.GREEN + "Reloaded dragondmg");
					break;
				case "settings":
                    sender.sendMessage(ChatColor.GREEN + "Currently loaded settings:");
                    sender.sendMessage(ChatColor.GRAY + "announceKiller = " + plugin.announceKiller);
                    sender.sendMessage(ChatColor.GRAY + "worldOnlyMsg = " + plugin.worldOnlyMsg);
                    sender.sendMessage(ChatColor.GRAY + "world = " + plugin.worldName);
                    sender.sendMessage(ChatColor.GRAY + "killReward = " + plugin.killReward);
                    sender.sendMessage(ChatColor.GRAY + "dmgReward = " + plugin.dmgReward);
                    sender.sendMessage(ChatColor.GRAY + "killRewards = " + plugin.killRewards.toString());
                    sender.sendMessage(ChatColor.GRAY + "dmgRewards = " + plugin.dmgRewards.toString());
                    break;
				default:
					sender.sendMessage(ChatColor.GREEN + "Commands:");
					sender.sendMessage(ChatColor.GRAY + "/dragondmg reload");
					sender.sendMessage(ChatColor.GRAY + "/dragondmg settings");
					break;
				}
			}else {
				sender.sendMessage(ChatColor.GREEN + "Commands:");
				sender.sendMessage(ChatColor.GRAY + "/dragondmg reload");
				sender.sendMessage(ChatColor.GRAY + "/dragondmg settings");
			}
			return true;
		}
		return false;
	}
	
}
