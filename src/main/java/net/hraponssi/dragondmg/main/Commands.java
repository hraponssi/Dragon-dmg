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
				default:
					sender.sendMessage(ChatColor.GREEN + "Commands:");
					sender.sendMessage(ChatColor.GRAY + "/dragondmg reload");
					break;
				}
			}else {
				sender.sendMessage(ChatColor.GREEN + "Commands:");
				sender.sendMessage(ChatColor.GRAY + "/dragondmg reload");
			}
			return true;
		}
		return false;
	}
	
}
