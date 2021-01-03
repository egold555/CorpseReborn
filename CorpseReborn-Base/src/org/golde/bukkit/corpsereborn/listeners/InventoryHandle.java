package org.golde.bukkit.corpsereborn.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.ServerVersion;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class InventoryHandle implements Listener{

	@EventHandler
	public void closeEvent(InventoryCloseEvent event){
		try{
			InventoryView iv = event.getView();
			CorpseData cd = Util.isCorpseInventory(iv);

			if(cd == null || !ConfigData.shouldDespawnAfterLoot()){
				return;
			}

			if(Util.isInventoryEmpty(iv.getTopInventory())){
				if(Main.serverVersion.getNiceVersion() != ServerVersion.v1_7){
					List<String> messages = ConfigData.finishLootingMessage(cd.getCorpseName());
					if(messages != null){
						
						event.getPlayer().sendMessage(getRandomString(messages));
					}
				}
				Util.removeCorpse(cd);
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}
	
	private String getRandomString(List<String> strings) {
		return strings.get(Main.getPlugin().getRandom().nextInt(strings.size()));
	}

}
