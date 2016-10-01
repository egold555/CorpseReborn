package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.Util;

public class RemoveCorpseRadius implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can run this command. Sorry about that.");
			return true;
		}
		if (!sender.hasPermission("corpses.remove")) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have enough permissions!");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Correct Usage: /" + commandLabel + " [radius]");
			return true;
		}
		
		double radius = Double.valueOf(args[0]);
		int removed = Util.removeCorpsesInRadius((Player) sender, radius);
		sender.sendMessage(ChatColor.GREEN + "Successfully removed " + removed + " corpse(s)!");
		return true;
	}
}
