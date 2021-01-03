package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerChangedWorld implements Listener {

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		try{
			for (CorpseData data : Main.getPlugin().corpses.getAllCorpses()) {
				if (data.getOrigLocation().getWorld()
						.equals(e.getPlayer().getWorld())) {
					data.setCanSee(e.getPlayer(), false);
					data.tickPlayerLater(Main.getPlugin().playerInitialTickDelay, e.getPlayer());
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

}
