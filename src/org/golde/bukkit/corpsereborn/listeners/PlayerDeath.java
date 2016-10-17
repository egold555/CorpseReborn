package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.PlayerInventoryClone;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerDeath implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (ConfigData.isOnDeath() && Util.playerInCorrectWorld(e.getEntity())) {
			CorpseData data;
			if (ConfigData.hasLootingInventory()) {
				/*
				Inventory inv = Bukkit.getServer().createInventory(null, 54,
						ConfigData.getInventoryName(e.getEntity()));
				inv.addItem(e.getDrops().toArray(
						new ItemStack[e.getDrops().size()]));
				Main.getPlugin().corpses.spawnCorpse(e.getEntity(), inv);
				e.getDrops().clear();
				*/
				String ver = Main.getPlugin().getServerVersion();
				
				PlayerInventoryClone inv = new PlayerInventoryClone(e.getEntity());
				if(!ver.startsWith("v1_8")){
					inv.setOffHand(e.getEntity().getInventory().getItemInOffHand());
				}
				data = Main.getPlugin().corpses.spawnCorpse(e.getEntity(), inv.toInventory()).setSelectedSlot(e.getEntity().getInventory().getHeldItemSlot());
				e.getDrops().clear();
			} else {
				data = Main.getPlugin().corpses.spawnCorpse(e.getEntity(), null);
			}
			
			Util.callEvent(new CorpseSpawnEvent(data, e.getEntity(), false));
		}
	}
}
