package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Updater;
import org.golde.bukkit.corpsereborn.dump.DumpException;
import org.golde.bukkit.corpsereborn.dump.ReportError;

public class GenericCommands implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args){
		try{
			String v = Main.getPlugin().getDescription().getVersion();
			if(args.length == 0){
				sender.sendMessage("This server is running CorpseReborn version " + v);
				Updater updater = new Updater("29875"); 
				Updater.UpdateResults result = updater.checkForUpdates();
				if(result.getResult() == Updater.UpdateResult.FAIL)
				{
					sender.sendMessage(ChatColor.RED + "Update checker failed to check for updates!");
				}
				else if(result.getResult() == Updater.UpdateResult.NO_UPDATE)
				{
					sender.sendMessage(ChatColor.GREEN + "Running latest stable version. (" + v + ")");
				}
				else if(result.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE)
				{
					sender.sendMessage(ChatColor.AQUA + "An update for CorpseReborn has been found!");
					sender.sendMessage(ChatColor.AQUA + "Current version: " + v +ChatColor.AQUA + ", new version: " + ChatColor.YELLOW + result.getVersion());
				}
				else if (result.getResult() == Updater.UpdateResult.DEV){
					sender.sendMessage(ChatColor.YELLOW + "You seem to have a version of the plugin that is not on spigot...");
					sender.sendMessage(ChatColor.RED + "Expect bugs!");
				}
				if(sender.hasPermission("corpses.reload") || sender.hasPermission("corpses.dump")){
					sender.sendMessage("/corpsereborn reload|dump");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("reload")){
				if (!sender.hasPermission("corpses.reload")) {
					sender.sendMessage(ChatColor.RED
							+ "You do not have enough permissions!");
					return true;
				}else{
					ConfigData.checkConfigForMissingOptions();
					ConfigData.load();
					sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
				}
			}

			if(args[0].equalsIgnoreCase("dump")){
				if (!sender.hasPermission("corpses.dump")) {
					sender.sendMessage(ChatColor.RED
							+ "You do not have enough permissions!");
					return true;
				}else{
					sender.sendMessage(ChatColor.GREEN + "Creating dump...");
					new ReportError(new DumpException(), sender);
					return true;
				}
			}


		}catch(Exception ex){
			new ReportError(ex, sender);
		}
		return true;
	}

}
