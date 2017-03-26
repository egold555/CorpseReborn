package org.golde.bukkit.corpsereborn.cmds;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseRemoveEvent;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class RemoveCorpseRadius implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		try{
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
				//sender.sendMessage(ChatColor.RED + "Correct Usage: /" + commandLabel + " [radius]");
				
				ArrayList<CorpseData> corpses = Util.removeAllCorpses(((Player)sender).getWorld());
				sender.sendMessage(ChatColor.GREEN + "Successfully removed all corpse(s) in the world. (" + corpses.size() + " of them!)");
				Util.callEvent(new CorpseRemoveEvent(corpses, true));
				return true;
			}

			double radius = Double.valueOf(args[0]);
			ArrayList<CorpseData> corpses = Util.removeCorpsesInRadius((Player) sender, radius);
			sender.sendMessage(ChatColor.GREEN + "Successfully removed " + corpses.size() + " corpse(s)!");
			Util.callEvent(new CorpseRemoveEvent(corpses, true));
		}catch(Exception ex){
			new ReportError(ex, sender);
		}
		return true;
	}
}
