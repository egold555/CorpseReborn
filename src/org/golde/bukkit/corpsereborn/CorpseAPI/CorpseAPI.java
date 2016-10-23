package org.golde.bukkit.corpsereborn.CorpseAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseAPI {

	public static CorpseData spawnCorpse(Player p, Location loc, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, ItemStack offHand)
	{
		CorpseData data = Main.getPlugin().corpses.spawnCorpse(p, loc, Util.makeNiceInv(p, mainInventory, helmet, chestplate, leggings, boots, hand, offHand));
		
		if (hand != null)
			data.setSelectedSlot(8); 
		Util.callEvent(new CorpseSpawnEvent(data, true));
		return data;
	}
	
	public static CorpseData spawnCorpse(Player p, Location loc, ItemStack[] mainInventory)
	{
		return spawnCorpse(p, loc, mainInventory, null, null, null, null, null, null);
	}
	
	public static CorpseData spawnCorpse(Player p, Location loc, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots)
	{
		return spawnCorpse(p, loc, mainInventory, helmet, chestplate, leggings, boots, null, null);
	}
	
	public static CorpseData spawnCorpse(Player p, Location loc, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand)
	{
		return spawnCorpse(p, loc, mainInventory, helmet, chestplate, leggings, boots, hand, null);
	}
	
	public static CorpseData spawnCorpse(Player p, Location loc){
		return spawnCorpse(p, loc, null, null, null, null, null, null, null);
	}


	public static void removeCorpse(CorpseData data){
		Main.getPlugin().corpses.removeCorpse(data);
	}
	
	
}
