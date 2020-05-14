package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e) {
		
		
		//Tells me if people are running CorpseReborn on there server.
		//Just fun to know who is running it.
		//Makes me feel good and happy inside lol
		new BukkitRunnable() {

			@Override
			public void run() {
				if(e.getPlayer().getUniqueId().toString().equals("e071b42d-e482-4bf7-a6e4-61ce59b281df")) {
					e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "This server is using CorpseReborn!");
				}
			}
			
		}.runTaskLater(Main.getPlugin(), 100);
		
		
		try{
			if(!ConfigData.getNewHitbox()) {Main.getPlugin().corpses.registerPacketListener(e.getPlayer());}
			for (CorpseData data : Main.getPlugin().corpses.getAllCorpses()) {
				if (data.getOrigLocation().getWorld()
						.equals(e.getPlayer().getLocation().getWorld())) {
					data.setCanSee(e.getPlayer(), false);
					data.tickPlayerLater(Main.getPlugin().playerInitialTickDelay, e.getPlayer());
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

}
