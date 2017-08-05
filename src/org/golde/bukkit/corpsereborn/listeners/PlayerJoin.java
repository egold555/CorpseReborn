package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
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
