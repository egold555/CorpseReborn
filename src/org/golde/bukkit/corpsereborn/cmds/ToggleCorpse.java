package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;

public class ToggleCorpse implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, Command arg1, String arg2, String[] arg3) {
		try{
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "Only players can run this command. Sorry about that.");
				return true;
			}
			if (!sender.hasPermission("corpses.toggle")) {
				sender.sendMessage(ChatColor.RED + "You do not have enough permissions!");
				return true;
			}
			Player p = (Player)sender;
			
			boolean canSee = Main.getPlugin().toggleCorpseForPlayer(p);
			String msg;
			
			msg = canSee ? "On" : "Off";
			
			sender.sendMessage(ChatColor.GREEN + "Corpses toggled " + msg + ".");
			
		}catch(Exception ex){
			new ReportError(ex, sender);
		}
		return true;
	}

}
