package org.golde.bukkit.corpsereborn.CorpseAPI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseAPI {

	public static CorpseData spawnCorpse(Player p, Inventory items){
		CorpseData data = Main.getPlugin().corpses.spawnCorpse(p, items);
		Util.callEvent(new CorpseSpawnEvent(data, p, true));
		return data;
	}
	
	public static CorpseData spawnCorpse(Player p){
		CorpseData data = Main.getPlugin().corpses.spawnCorpse(p, null);
		Util.callEvent(new CorpseSpawnEvent(data, p, true));
		return data;
	}
	
	public static void removeCorpse(CorpseData data){
		Main.getPlugin().corpses.removeCorpse(data);
	}
}
