package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;

public class PlayerDeath implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (ConfigData.isOnDeath() && Util.playerInCorrectWorld(e.getEntity())) {
			if (ConfigData.hasLootingInventory()) {
				Inventory inv = Bukkit.getServer().createInventory(null, 54,
						ConfigData.getInventoryName(e.getEntity()));
				inv.addItem(e.getDrops().toArray(
						new ItemStack[e.getDrops().size()]));
				Main.getPlugin().corpses.spawnCorpse(e.getEntity(), inv);
				e.getDrops().clear();
			} else {
				Main.getPlugin().corpses.spawnCorpse(e.getEntity(), null);
			}
		}
	}
}
