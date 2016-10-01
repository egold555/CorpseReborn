package org.golde.bukkit.corpsereborn;

import java.util.List;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.golde.bukkit.corpsereborn.cmds.*;
import org.golde.bukkit.corpsereborn.listeners.*;
import org.golde.bukkit.corpsereborn.nms.Corpses;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class Main extends JavaPlugin {

	private static Main plugin;

	public Corpses corpses;
	public boolean cont = true;

	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		Util.info("Loading corpses creator...");
		loadCorpsesCreator();
		Util.info("Loading config data...");
		ConfigData.load();
		if (!cont) {
			return;
		}
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerRespawn(), this);
		pm.registerEvents(new PlayerChangedWorld(), this);
		pm.registerEvents(new PlayerDeath(), this);
		getCommand("spawncorpse").setExecutor(new SpawnCorpse());
		getCommand("removecorpse").setExecutor(new RemoveCorpseRadius());
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
		return true;
	}

	private void loadCorpsesCreator() {
		String version = getServerVersion();
		if (isVersionSupported(version)) {
			try {
				Class<?> subClass = Class
						.forName("org.golde.bukkit.corpsereborn.nms.nmsclasses.NMSCorpses_"
								+ version);
				corpses = (Corpses) subClass.getConstructor().newInstance();
				Util.info("Corpses creator loaded.");
				//Util.info("NMSCorpses_"+ version);
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
