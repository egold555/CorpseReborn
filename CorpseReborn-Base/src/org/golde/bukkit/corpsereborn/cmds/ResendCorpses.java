package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class ResendCorpses implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		try{
			
			if (!sender.hasPermission("corpses.resend")) {
				sender.sendMessage(ChatColor.RED
						+ "You do not have enough permissions!");
				return true;
			}
			
			for(final CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
				new BukkitRunnable(){
					public void run(){
						sender.sendMessage(ChatColor.GREEN + "Resending corpse: " + cd.getEntityId());
						cd.resendCorpseToEveryone();
					}
				}.runTaskLater(Main.getPlugin(), 2);
			}
		}catch(Exception ex){
			new ReportError(ex, sender);
		}
		return true;
	}

}
