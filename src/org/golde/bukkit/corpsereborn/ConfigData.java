package org.golde.bukkit.corpsereborn;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class ConfigData {

	private static int corpseTime;
	private static boolean onDeath;
	private static boolean lootingInventory;
	private static boolean showTags;
	private static String worldName;
	private static World world;

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
	
	public static World getWorld(){
		return world;
	}

	public static void load() {
		try {
			corpseTime = Main.getPlugin().getConfig().getInt("corpse-time");
			onDeath = Main.getPlugin().getConfig().getBoolean("on-death");
			lootingInventory = Main.getPlugin().getConfig().getBoolean("looting-inventory");
			showTags = Main.getPlugin().getConfig().getBoolean("show-tags");
			worldName = Main.getPlugin().getConfig().getString("world");
			if(worldName.equalsIgnoreCase("all")){
				world = null;
			}else{
				if(Bukkit.getWorld(worldName) != null){
					world = Bukkit.getWorld(worldName);
				}else{
					world = null;
					Util.severe("================================");
					Util.severe("Could not find the world: " + worldName);
					Util.severe("Defaulting to ALL WORLDS");
					Util.severe("================================");
				}
				Util.info("Config successfully loaded.");
			}

		} catch (Exception e) {
			Util.severe("================================");
			Util.severe("Could not load config!");
			Util.severe("Is it configured properly?");
			Util.severe("Have you deleted old configs?");
			Util.severe("================================");
			Main.getPlugin().cont = false;
			Bukkit.getServer().getPluginManager().disablePlugin(Main.getPlugin());
		}
	}

}
