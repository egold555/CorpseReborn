package org.golde.bukkit.corpsereborn.dump;

import java.io.IOException;

import me.nrubin29.pastebinapi.CreatePaste;
import me.nrubin29.pastebinapi.ExpireDate;
import me.nrubin29.pastebinapi.PastebinAPI;
import me.nrubin29.pastebinapi.PastebinException;
import me.nrubin29.pastebinapi.PrivacyLevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.Main;
public class ReportError {

	public ReportError(Exception e){
		this(e, Bukkit.getConsoleSender());
	}
	
	public ReportError(final Exception e, final CommandSender cs){
		//makes a new dump and sends the user the link
		final DumpTemplate dt = new DumpTemplate(e);
		new BukkitRunnable(){
			public void run(){
				makeDump(cs, dt, e);
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	private void makeDump(final CommandSender sender, final DumpTemplate dt, final Exception e){
		PastebinAPI api = new PastebinAPI("bcfd9fe9a975802e3b494234ebaa1c25");
		CreatePaste paste = api.createPaste();
		paste.withText(dt.output());
		paste.withPrivacyLevel(PrivacyLevel.PUBLIC);
		paste.withExpireDate(ExpireDate.NEVER);
		paste.withName("CorpseReborn Error Paste");
		
		try {
			String url = paste.post();
			if(dt.isFromDumpCommand()){
				sender.sendMessage(ChatColor.GREEN + "Dump has been created.");
			}else{
				sender.sendMessage(ChatColor.RED + "Uh Oh. It looks like an error occurred with CorpseReborn. Please screenshot this and send it to ericgolde555 on SpigotMC or email to plugins@golde.org");
			}
			
			sender.sendMessage(ChatColor.YELLOW + url);
		} catch (PastebinException e1) {
			sender.sendMessage(ChatColor.RED + "Failed to dump, please check the console for more information.");
			e.printStackTrace();
		} catch (IOException e1) {
			sender.sendMessage(ChatColor.RED + "Failed to dump, please check the console for more information.");
			e.printStackTrace();
		}
		if(Main.getPlugin().isDev){
			e.printStackTrace();
		}
		
	}
}


