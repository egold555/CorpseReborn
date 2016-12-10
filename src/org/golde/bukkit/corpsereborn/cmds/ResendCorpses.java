package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class ResendCorpses implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		for(final CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			new BukkitRunnable(){
				public void run(){
					Bukkit.broadcastMessage(ChatColor.GREEN + "Resending corpse: " + cd.getEntityId());
					cd.resendCorpseToEveryone();
				}
			}.runTaskLater(Main.getPlugin(), 2);
		}
		return true;
	}

}
