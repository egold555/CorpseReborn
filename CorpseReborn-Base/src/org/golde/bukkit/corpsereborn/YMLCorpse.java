package org.golde.bukkit.corpsereborn;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.nms.Corpses;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class YMLCorpse {

	public static void loadAndSpawnAllCorpses(Corpses rawCorpse, Configuration config) {
		int amountOfCorpses = config.getInt("amount-of-corpses");

		for(int i = 0; i < amountOfCorpses; i++) {
			String first = i + ".";
			Location loc = (Location)config.get(first + "location");
			String name = config.getString(first + "name");
			String skin = config.getString(first + "skin");
			if(skin == null) {
				skin = "";
			}
			int rotation = config.getInt(first + "rotation");
			int selectedSlot = config.getInt(first + "slot");

			Inventory inv = Bukkit.createInventory(null, PlayerInventoryClone.INVENTORY_SIZE);
			ConfigurationSection invConfig = config.getConfigurationSection(first + "inventory");
			if(invConfig != null) {
				Set<String> keys = invConfig.getKeys(false);
				for(String key:keys) {
					int slot = Integer.parseInt(key);
					ItemStack itemStack = (ItemStack)invConfig.get(key);
					inv.setItem(slot, itemStack);
				}
			}

			CorpseData data = rawCorpse.loadCorpse(name, skin, loc, inv, rotation);
			if(data != null) {
				data.setSelectedSlot(selectedSlot);
			}
		}

	}


	public static void save(List<CorpseData> list, Configuration config) {
		int tempCounter = 0;
		config.set("VERSION", Main.serverVersion.name());
		config.set("amount-of-corpses", list.size());
		
		for(CorpseData d:list) {
			String first = tempCounter + ".";

			config.set(first + "location", d.getOrigLocation());
			config.set(first + "name", d.getCorpseName());
			if(d.getProfilePropertiesJson() != null) {
				config.set(first + "skin", d.getProfilePropertiesJson());
			}
			config.set(first + "rotation", d.getRotation());
			config.set(first + "slot", d.getSelectedSlot());
			Inventory inv = d.getLootInventory();

			for(int slot = 0; slot < inv.getSize(); slot++) {
				ItemStack item = inv.getItem(slot);
				if(item != null) {
					config.set(first + "inventory." + slot, item);
				}
			}

			tempCounter++;
		}
	}


}
