package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		try{
			for (CorpseData data : Main.getPlugin().corpses.getAllCorpses()) {
				if (data.getOrigLocation().getWorld()
						.equals(e.getRespawnLocation().getWorld())) {
					data.setCanSee(e.getPlayer(), false);
					data.tickPlayerLater(Main.getPlugin().playerInitialTickDelay, e.getPlayer());
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

}
