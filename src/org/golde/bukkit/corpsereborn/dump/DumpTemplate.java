package org.golde.bukkit.corpsereborn.dump;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;

public class DumpTemplate {
	
	private final String NEW_LINE = "\n";
	private final String exception;
	private final boolean isFromDumpCommand;
	
	public DumpTemplate(Exception ex){
		if(ex instanceof DumpException){
			exception = "(Dump Command)";
			isFromDumpCommand = true;
			return;
		}
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		exception = w.toString();
		isFromDumpCommand = false;
	}
	
	private String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private String getPlugins(){
		String toReturn = "";
		for(Plugin pl:Bukkit.getPluginManager().getPlugins()){
			toReturn = toReturn + pl.getName()  + " - " + pl.getDescription().getVersion() + NEW_LINE;
		}
		return toReturn;
	}
	
	private String getConfig() {
		/*IOException ex;
		File config = new File(Main.getPlugin().getDataFolder(), "config.yml");
		try {
			return Files.toString(config, Charset.defaultCharset()).replaceAll("\"", "");
		} catch (IOException e) {
			e.printStackTrace();
			ex = e;
		}
		return "Could not read Config File: " + ex.getMessage();*/
		FileConfiguration config = Main.getPlugin().getConfig();
		String cfg = "";
		cfg = cfg + "enable-update-checker: " + ConfigData.shouldCheckForUpdates() + NEW_LINE;
		cfg = cfg + "corpse-time: " + ConfigData.getCorpseTime() + NEW_LINE;
		cfg = cfg + "on-death: " + ConfigData.isOnDeath() + NEW_LINE;
		cfg = cfg + "looting-inventory: " + ConfigData.hasLootingInventory() + NEW_LINE;
		cfg = cfg + "despawn-after-looted: " + ConfigData.shouldDespawnAfterLoot() + NEW_LINE;
		cfg = cfg + "show-tags: " + ConfigData.showTags() + NEW_LINE;
		cfg = cfg + "world: " + config.getString("world") + NEW_LINE;
		cfg = cfg + "gui-title: " + config.getString("gui-title") + NEW_LINE;
		cfg = cfg + "username-format: " + config.getString("username-format") + NEW_LINE;
		cfg = cfg + "finish-looting-message: " + config.getString("finish-looting-message") + NEW_LINE;
		cfg = cfg + "new-hitboxes: " + ConfigData.getNewHitbox() + NEW_LINE;
		cfg = cfg + "render-armor: " + ConfigData.shouldRenderArmor();
		return cfg;
	}
	
	@SuppressWarnings("static-access")
	public String output(){
		
		return "Date: " + getDate() + 
				NEW_LINE +
				"CorpseReborn Version: " + Main.getPlugin().getDescription().getVersion().split(" ")[0] + 
				NEW_LINE + 
				NEW_LINE + 
				"Server Version: " + Bukkit.getVersion() + 
				NEW_LINE +
				"Server Type: " + Main.getPlugin().serverType.name() +
				NEW_LINE + 
				"Folder Version: " + Main.getPlugin().serverVersion.name() + 
				NEW_LINE +
				NEW_LINE + 
				"Exception: " + 
				NEW_LINE +
				exception +
				NEW_LINE +
				NEW_LINE +
				"Plugins: " +
				NEW_LINE + 
				getPlugins()+
				NEW_LINE +
				"Config: " +
				NEW_LINE +
				getConfig().replaceAll("&", "*");
				
				

	}

	public boolean isFromDumpCommand() {
		return isFromDumpCommand;
	}
}
