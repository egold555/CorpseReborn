package org.golde.bukkit.corpsereborn;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.cmds.GenericCommands;
import org.golde.bukkit.corpsereborn.cmds.RemoveCorpseRadius;
import org.golde.bukkit.corpsereborn.cmds.ResendCorpses;
import org.golde.bukkit.corpsereborn.cmds.SpawnCorpse;
import org.golde.bukkit.corpsereborn.cmds.ToggleCorpse;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.listeners.ChunkCorpseFix;
import org.golde.bukkit.corpsereborn.listeners.CowHit;
import org.golde.bukkit.corpsereborn.listeners.InventoryHandle;
import org.golde.bukkit.corpsereborn.listeners.PlayerChangedWorld;
import org.golde.bukkit.corpsereborn.listeners.PlayerDeath;
import org.golde.bukkit.corpsereborn.listeners.PlayerJoin;
import org.golde.bukkit.corpsereborn.listeners.PlayerRespawn;
import org.golde.bukkit.corpsereborn.listeners.WorldguardListener;
import org.golde.bukkit.corpsereborn.nms.Corpses;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin {

	private static Main plugin;

	private HashSet<String> whoCanNotSeeCorpses = new HashSet<String>();

	public final int playerInitialTickDelay = 35;  

	public Corpses corpses;
	public boolean cont = true;
	public final boolean isDev = true; //TODO: CHANGE BEFORE RELEASE
	public static ServerVersion serverVersion = ServerVersion.UNKNOWN;
	public static ServerType serverType = ServerType.UNKNOWN;

	public boolean isWorldGuardEnabled = false;
	public WorldGuardPlugin worldGuard = null;

	public WorldguardListener worldGuardListener;

	public File corpseSaveFile;
	
	private static final Random RANDOM = new Random();

	@Override
	public void onLoad() {
		PluginManager pm = getServer().getPluginManager();
		
		//For this release, I am disabling WorldGuard until I can get support for it.
		
//		if(pm.getPlugin("WorldGuard") != null) {
//			try {
//				Util.info("Worldguard detected. Adding custom spawn flags");
//				worldGuard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
//				worldGuardListener = new WorldguardListener(worldGuard);
//				isWorldGuardEnabled = true;
//			}
//			catch(Exception e) {
//				Util.info("Only worldguard 6.2 or later can use flags! Disabling worldguard support!");
//				isWorldGuardEnabled = false;
//			}
//		}
	}

	@Override
	public void onEnable() {
		try{
			plugin = this;
			serverType = ServerType.whatAmI(this);
			Util.cinfo("");
			Util.cinfo("");
			Util.cinfo("&dHi!");
			Util.cinfo("&dThank you for downloading the new beta 1.14.3 update.");
			Util.cinfo("&dPlease note that I have &cDisabled WorldGuard support &dfor this update entirely.");
			Util.cinfo("&dI am working on a new System to support all versions of WorldGuard,");
			Util.cinfo("&dbut I wanted to get this update out ASAP for everyone.");
			Util.cinfo("&dIf you find any bugs, please let me know by either filling out a bug report on GitHub");
			Util.cinfo("&bhttps://github.com/egold555/CorpseReborn/issues &dor reporting them");
			Util.cinfo("&dto me on Spigot: &bhttps://www.spigotmc.org/resources/corpsereborn.29875/");
			Util.cinfo("");
			Util.cinfo("&d--Eric Golde");
			Util.cinfo("");
			Util.cinfo("");
			
			if(!serverType.isCompatible() && !isDev){
				Util.cinfo("&e====================================================");
				Util.cinfo("&cHello! You seem to be using a untested server type: ");
				Util.cinfo("&b" + ServerType.getSupportedVersions());
				Util.cinfo("&cYou are running: &e" + serverType.name());
				Util.cinfo("&cExpect things to not work as they were intended too.");
				Util.cinfo("&e====================================================");
			}

			//saveDefaultConfig();
			ConfigData.checkConfigForMissingOptions();

			loadCorpsesCreator();
			ConfigData.load();
			corpseSaveFile = new File(getDataFolder(), "corpses.yml");

			if(!isDev) {checkForUpdates();}

			if(serverVersion == ServerVersion.UNKNOWN && !isDev){
				Util.cinfo("&e====================================================");
				Util.cinfo("&cIt seems like you are using a untested version that I have not explored in detail of why it might not work. If you could please Private Message me on spigot the following (In blue) so I can check out in more detail why this version might not be compatable that would be fantastic :)");
				Util.cinfo("&b" + Bukkit.getVersion());
				Util.cinfo("&e====================================================");
			}

			if(isDev){
				Util.cinfo("&e====================================================");
				Util.cinfo("&cDev mode activated. Turn this off before release!");
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
			getCommand("togglecorpse").setExecutor(new ToggleCorpse());

			if(isWorldGuardEnabled) {
				try {
					worldGuardListener.registerEvents(pm);
				}catch(Exception e) {
					Util.info("Only worldguard 6.2 or later can use flags! Disabling worldguard support!");
					isWorldGuardEnabled = false;
				}
			}

			// Removing stray cows after 2 ticks, and every minute.
			new BukkitRunnable(){
				public void run(){
					Util.removeBuggedCows();
				}
			}.runTaskTimer(this, 2, 20*60);

			new BukkitRunnable(){
				public void run(){
					corpses.updateCows();
				}
			}.runTaskTimer(this, 0, 20);

			if(ConfigData.shouldSaveCorpses()) {
				if(corpseSaveFile.exists()){
					YamlConfiguration corpseConfig = YamlConfiguration.loadConfiguration(corpseSaveFile);
					if(ServerVersion.valueOf(corpseConfig.getString("VERSION")) == Main.serverVersion) { //Different versions save different things to config

						YMLCorpse.loadAndSpawnAllCorpses(corpses, corpseConfig);
					}
					else {
						Util.info("Looks like you changed server versions. I have made a backup of your corpses.yml just in case.");
						Util.copyFiles(corpseSaveFile, new File(getDataFolder(), "corpses.yml.backup"));
//						Util.info("Removing old corpses.yml because you updated your server!");
						corpseConfig.set("VERSION", Main.serverVersion.name());
						try {
							corpseConfig.save(corpseSaveFile);
						} catch (IOException e) {
							//new ReportError(e);
							e.printStackTrace();
						}
					}

				}
			}

			//if(!isDev) {sendCoolDataToEric();}



		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	@Override
	public void onDisable(){

		if(ConfigData.shouldSaveCorpses()) {
			YamlConfiguration corpseSave = new YamlConfiguration();
			YMLCorpse.save(corpses.getAllCorpses(), corpseSave);
			try {
				corpseSave.save(corpseSaveFile);
			} catch (IOException e) {
				//new ReportError(e);
				e.printStackTrace();
			}
		}

		try{ //TODO: Null pointer if version is not found
			//remove all cows
			if(corpses != null) {
				corpses.removeAllCows();
			}
		}
		catch(Exception ex){
			//new ReportError(ex);
			ex.printStackTrace();
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

	@Deprecated
	private void sendCoolDataToEric() { //TODO: Fix stats page
		if(ConfigData.shouldSendDataToEric())
			try {

				HttpURLConnection con = (HttpURLConnection) new URL("http://web2.golde.org/files/spigot/CorpseReborn/stats/write.php?v=" + serverVersion.name() + "&t=" + serverType.name()).openConnection();

				// optional default is GET
				con.setRequestMethod("GET");

				//add request header
				con.setRequestProperty("User-Agent", "Mozilla/5.0");

				int responseCode = con.getResponseCode();
				if(responseCode == 200) {
					Util.info("Successfully sent stats!");
				}else {
					Util.warning("Failed to send stats: Responce Code:" + responseCode);
				}

			}catch(Exception e) {
				Util.warning("Eric's server seems to be down. I can not send stats to it!");
			}

	}

	public boolean shouldPlayerSeeCorpse(Player p) {
		return !whoCanNotSeeCorpses.contains(p.getUniqueId().toString());
	}

	public boolean toggleCorpseForPlayer(Player p) {
		boolean result;
		String uuid = p.getUniqueId().toString();
		if(whoCanNotSeeCorpses.contains(uuid)) {
			whoCanNotSeeCorpses.remove(uuid);
			result = true;
		}else {
			whoCanNotSeeCorpses.add(uuid);
			result = false;
		}
		return result;
	}
	
	public Random getRandom() {
		return RANDOM;
	}
}
