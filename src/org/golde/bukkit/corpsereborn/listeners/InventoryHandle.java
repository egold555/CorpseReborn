package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class InventoryHandle implements Listener{

	@EventHandler
	public void closeEvent(InventoryCloseEvent event){
		InventoryView iv = event.getView();
		CorpseData cd = Util.isCorpseInventory(iv);
		
		if(cd == null || !ConfigData.shouldDespawnAfterLoot()){
			return;
		}

		if(Util.isInventoryEmpty(iv.getTopInventory())){
			String message = ConfigData.finishLootingMessage(cd.getUsername());
			if(message != null){
				event.getPlayer().sendMessage(message);
			}
			Util.removeCorpse(cd);
		}
	}
	
}
