package org.golde.bukkit.corpsereborn.dump;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;

public class ReportError {

	public ReportError(Exception e){
		this(e, Bukkit.getConsoleSender());
	}
	
	public ReportError(final Exception e, final CommandSender cs){
		//makes a new dump and sends the user the link
		final DumpTemplateMarkdown dt = new DumpTemplateMarkdown(e);
		new BukkitRunnable(){
			public void run(){
				makeDump(cs, dt, e);
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	private void makeDump(final CommandSender sender, final DumpTemplateMarkdown dt, final Exception e){
		if(Main.getPlugin().isDev){
			e.printStackTrace();
			Util.cinfo("");
			Util.cinfo("");
			System.out.println(dt.getOutput());
			Util.cinfo("");
			Util.cinfo("");
		}
		
		try {
			String url = CRDumpHTTPApi.paste(dt.getOutput());
			if(dt.isFromDumpCommand()){
				sender.sendMessage(ChatColor.GREEN + "Dump has been created.");
			}else{
				sender.sendMessage(ChatColor.RED + "Uh Oh. It looks like an error occurred with CorpseReborn. Please screenshot this and send it to ericgolde555 on SpigotMC or email to plugins@golde.org");
			}
			
			sender.sendMessage(ChatColor.YELLOW + url);
		}
		catch (IOException e1) {
			sender.sendMessage(ChatColor.RED + "Failed to dump, please check the console for more information.");
			e.printStackTrace();
		}

		
		
	}
}


