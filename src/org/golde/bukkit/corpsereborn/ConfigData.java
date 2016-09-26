package org.golde.bukkit.corpsereborn;

import org.bukkit.Bukkit;

public class ConfigData {

	private static int corpseTime;
	private static boolean onDeath;
	private static boolean lootingInventory;
	private static boolean showTags;

	public static int getCorpseTime() {
		return corpseTime;
	}

	public static boolean isOnDeath() {
		return onDeath;
	}

	public static boolean hasLootingInventory() {
		return lootingInventory;
	}
	
	public static boolean showTags() {
		return showTags;
	}

	public static void load() {
		try {
			corpseTime = Main.getPlugin().getConfig().getInt("corpse-time");
			onDeath = Main.getPlugin().getConfig().getBoolean("on-death");
			lootingInventory = Main.getPlugin().getConfig()
					.getBoolean("looting-inventory");
			showTags = Main.getPlugin().getConfig()
					.getBoolean("show-tags");
		} catch (Exception e) {
			Bukkit.getServer().getLogger()
					.severe("================================");
			Bukkit.getServer().getLogger().severe("Could not load config!");
			Bukkit.getServer().getLogger().severe("Is it configured properly?");
			Bukkit.getServer().getLogger()
					.severe("Have you deleted old configs?");
			Bukkit.getServer().getLogger()
					.severe("================================");
			Main.getPlugin().cont = false;
			Bukkit.getServer().getPluginManager()
					.disablePlugin(Main.getPlugin());
		}
	}

}
