package org.golde.bukkit.corpsereborn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.dump.ReportError;

public class Lang {

	private static String guiName;
	private static String username;
	private static List<String> finishLootingMessage = new ArrayList<String>();

	public static String getInventoryName(Player p){return guiName.replaceAll("%corpse%", p.getName()).replaceAll("&", "§");}

	@Deprecated
	public static String getUsername(Player p, String overrideUsername){
		return getUsername(p.getName(), overrideUsername);
	}

	public static String getUsername(String pUsername, String overrideUsername){
		if(overrideUsername == null){
			overrideUsername = username.replaceAll("%corpse%", pUsername).replaceAll("&", "§");
		}

		if(overrideUsername.length() > 16) {
			overrideUsername = overrideUsername.substring(0, 16);
		}
		return overrideUsername;
	}
	public static List<String> finishLootingMessage(String name){
		if(finishLootingMessage.size() == 0) {
			return null;
		}

		if(finishLootingMessage.get(0).equalsIgnoreCase("none")){
			return null;
		}

		List<String> toReturn = new ArrayList<String>();

		for(String repl : finishLootingMessage) {
			toReturn.add(repl.replaceAll("%corpse%", name).replaceAll("&", "§"));
		}

		return toReturn;
	}

	public static void checkConfigForMissingOptions()
	{
		FileConfiguration config = Main.getPlugin().getConfig();

		if (! config.isSet("gui-title")) {
			appendConfig("#Title of inventory created when you click the corpses head to loot it. ",
					"#%corpse% gets replaced with the corpses name.",
					"#Color codes and unicode characters work, but might be glitchy.",
					"#Minecraft has a 32 character limit on GUI lengths. If your title is more then that you will get a error on the console.",
					"gui-title: \"%corpse%'s Items\"");
		}

		if (! config.isSet("username-format")) {
			appendConfig("#This is the username of the corpse.",
					"#%corpse% gets replaced with the corpses name.",
					"#Color codes and unicode characters work, but might be glitchy.",
					"#Minecraft has a 16 character limit on name lengths. If your title is more then that you will get a error on the console.",
					"username-format: \"%corpse%\"");
		}

		if (! config.isSet("finish-looting-message")) {
			appendConfig("#This is the message sent when you finish looting the corpse. Set the message to \"none\" to disable.",
					"#%corpse% gets replaced with the corpses name.",
					"#Color codes and unicode characters work, but might be glitchy.",
					"finish-looting-message: \"&bYou have finished looting %corpse%'s corpse.\"");
		}

	}

	private static String newLine = "\r\n";

	private static void appendConfig(String... lines)
	{
		File dataDir = Main.getPlugin().getDataFolder();
		if (!dataDir.exists())
			dataDir.mkdirs();

		File file = new File(dataDir, "config.yml");

		try {
			FileWriter writer = new FileWriter(file, true);
			writer.append(newLine);
			for (String line: lines) {

				writer.append(line);
				writer.append(newLine);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			FileConfiguration config = Main.getPlugin().getConfig();

			guiName = config.getString("gui-title", "%corpse%'s Items");
			username = config.getString("username-format", "%corpse%");
			String finishLootingMessageString = config.getString("finish-looting-message", "&bYou have finished looting %corpse%'s corpse.");

			//work around for upgrading configs. finish-looting-message can now also support mutiple messages

			if(finishLootingMessageString.charAt(0) == '[' && finishLootingMessageString.charAt(finishLootingMessageString.length() - 1) == ']') {
				finishLootingMessage = config.getStringList("finish-looting-message");
			}
			else {
				finishLootingMessage.add(finishLootingMessageString);
			}
			
			Util.info("Config successfully loaded.");

		} catch (Exception e) {
			Util.severe("================================");
			Util.severe("Could not load language file!");
			Util.severe("Is it configured properly?");
			Util.severe("Have you deleted old configs?");
			Util.severe("================================");
			new ReportError(e);
			Main.getPlugin().cont = false;
			Bukkit.getServer().getPluginManager().disablePlugin(Main.getPlugin());
		}
	}

}
