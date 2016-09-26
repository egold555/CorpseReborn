package org.golde.bukkit.corpsereborn;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseAPI {

	public static void spawnCorpse(Player p, Inventory items){
		Main.getPlugin().corpses.spawnCorpse(p, items);
	}
	
	public static CorpseData spawnCorpse(Player p){
		return Main.getPlugin().corpses.spawnCorpse(p, null);
	}
	
	public static void removeCorpse(CorpseData data){
		Main.getPlugin().corpses.removeCorpse(data);
	}
	
}
