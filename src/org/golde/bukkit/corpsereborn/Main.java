package org.golde.bukkit.corpsereborn;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.cmds.GenericCommands;
import org.golde.bukkit.corpsereborn.cmds.RemoveCorpseRadius;
import org.golde.bukkit.corpsereborn.cmds.ResendCorpses;
import org.golde.bukkit.corpsereborn.cmds.SpawnCorpse;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.listeners.ChunkCorpseFix;
import org.golde.bukkit.corpsereborn.listeners.InventoryHandle;
import org.golde.bukkit.corpsereborn.listeners.PlayerChangedWorld;
import org.golde.bukkit.corpsereborn.listeners.PlayerDeath;
import org.golde.bukkit.corpsereborn.listeners.PlayerJoin;
import org.golde.bukkit.corpsereborn.listeners.PlayerRespawn;
import org.golde.bukkit.corpsereborn.listeners.CowHit;
import org.golde.bukkit.corpsereborn.nms.Corpses;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class Main extends JavaPlugin {

	private static Main plugin;

	public final int playerInitialTickDelay = 35;  
	
	public Corpses corpses;
	public boolean cont = true;
	public boolean isDev = false;
	public static ServerVersion serverVersion = ServerVersion.UNSUPPORTED_SERVER_VERSION;
	public static ServerType serverType = ServerType.UNKNOWN;
	public void onEnable() {
		try{
			plugin = this;
			serverType = ServerType.whatAmI(this);
			if(!serverType.isCompatible()){
				Util.cinfo("&e====================================================");
				Util.cinfo("&cIt seems like you are not running a supported version of server. This plugin only supports: ");
				Util.cinfo("&b" + ServerType.getSupportedVersions());
				Util.cinfo("&cYou are running: &e" + serverType.name());
				Util.cinfo("&cExpect things to not work as they were intended too.");
				Util.cinfo("&eYOU HAVE BEEN WARNED!");
				Util.cinfo("&e====================================================");
			}

			//saveDefaultConfig();
			ConfigData.checkConfigForMissingOptions();

			loadCorpsesCreator();
			ConfigData.load();
			checkForUpdates();
			if(serverVersion == ServerVersion.UNSUPPORTED_SERVER_VERSION){
				Util.cinfo("&e====================================================");
				Util.cinfo("&cIt seems like you are using a untested version that I have not explored in detail of why it might not work. If you could please Private Message me on spigot the following (In blue) so I can check out in more detail why this version might not be compatable that would be fantastic :)");
				Util.cinfo("&b" + Bukkit.getVersion());
				Util.cinfo("&e====================================================");
			}
			if (!cont) {
				return;
			}
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(new PlayerJoin(), this);
			pm.registerEvents(new PlayerRespawn(), this);
			pm.registerEvents(new PlayerChangedWorld(), this);
			pm.registerEvents(new PlayerDeath(), this);
			pm.registerEvents(new InventoryHandle(), this);
			pm.registerEvents(new ChunkCorpseFix(), this);
			if(serverVersion.getNiceVersion() != ServerVersion.v1_7){
				pm.registerEvents(new CowHit(), this);
			}


			getCommand("spawncorpse").setExecutor(new SpawnCorpse());
			getCommand("removecorpse").setExecutor(new RemoveCorpseRadius());
			getCommand("corpsereborn").setExecutor(new GenericCommands());
			getCommand("resendcorpses").setExecutor(new ResendCorpses());
			new BukkitRunnable(){
				public void run(){
					Util.removeBuggedCows();
				}
			}.runTaskLater(this, 2);

			new BukkitRunnable(){
				public void run(){
					corpses.updateCows();
				}
			}.runTaskTimer(this, 0, 20);
			
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	public void onDisable(){
		try{
			//remove all cows
			corpses.removeAllCows();
		}catch(Exception ex){
			new ReportError(ex);
		}
		
	}

	void checkForUpdates(){
		Updater updater = new Updater("29875"); 
		Updater.UpdateResults result = updater.checkForUpdates();
		if(result.getResult() == Updater.UpdateResult.FAIL)
		{
			Util.severe("Update checker failed to check for updates!");
			Util.info("Stacktrace: " + result.getVersion());
		}
		else if(result.getResult() == Updater.UpdateResult.NO_UPDATE)
		{
			Util.info("No update available");
		}
		else if(result.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE)
		{
			Util.cinfo("&aAn update for CorpseReborn has been found!");
			Util.cinfo("&bCurrent version: &e" + getDescription().getVersion() + "&b, new version: &e" + result.getVersion());
		}
		else if (result.getResult() == Updater.UpdateResult.DEV){
			Util.cinfo("&eYou seem to have a version of the plugin that is not on spigot...");
			Util.cinfo("&cExpect bugs!");
			isDev = true;
		}
	}

	private String getServerVersion() {
		return getServer().getClass().getName().split("\\.")[3];
	}

	private boolean isVersionSupported(String version) {
		try {
			Class.forName("org.golde.bukkit.corpsereborn.nms.nmsclasses.NMSCorpses_"
					+ version, false, getClassLoader());
		} catch (Exception e) {
			return false;
		}
		serverVersion = ServerVersion.fromClass(version);
		return true;
	}

	public void loadCorpsesCreator() {
		String version = getServerVersion();
		if (isVersionSupported(version)) {
			try {
				Class<?> subClass = Class
						.forName("org.golde.bukkit.corpsereborn.nms.nmsclasses.NMSCorpses_"
								+ version);
				corpses = (Corpses) subClass.getConstructor().newInstance();
				Util.info("Version: " + serverVersion);
			} catch (Exception e) {
				Util.severe("================================");
				Util.severe("There was a problem with loading the corpses creator!");
				Util.severe("================================");
				cont = false;
				getServer().getPluginManager().disablePlugin(this);
			}
		} else {
			Util.severe("================================");
			Util.severe("Server version is not supported!");
			Util.severe("You are running " + version + ".");
			Util.severe("The supported versions are: " + getAppendedVersionsSupported() + ".");
			Util.severe("================================");
			cont = false;
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private String getAppendedVersionsSupported() {
		try {
			StringBuilder sb = new StringBuilder();
			List<ClassInfo> classes = ClassPath.from(getClassLoader())
					.getTopLevelClasses("org.golde.bukkit.corpsereborn.nms.nmsclasses")
					.asList();
			for (int i = 0; i < classes.size(); i++) {
				if (classes.get(i).getSimpleName().startsWith("NMSCorpses_")) {
					sb.append(classes.get(i).getSimpleName()
							.replace("NMSCorpses_", ""));
					if (i != classes.size() - 1) {
						sb.append(", ");
					}
				}
			}
			return sb.toString();
		} catch (Exception e) {
			return "Error finding versions";
		}
	}

	public static Main getPlugin() {
		return plugin;
	}
}
