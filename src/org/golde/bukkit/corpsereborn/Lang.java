package org.golde.bukkit.corpsereborn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.golde.bukkit.corpsereborn.dump.ReportError;

public class Lang {

	private static YamlConfiguration LANG_YML;
	private static File LANG_FILE;
	
	public enum Msgs {
	    NO_PERMS("no-permissions", "&cYou don''t have permission for that!");
	 
	    private String path;
	    private String def;
	    private static YamlConfiguration LANG;
	 
	    /**
	    * Lang enum constructor.
	    * @param path The string path.
	    * @param start The default string.
	    */
	    Msgs(String path, String start) {
	        this.path = path;
	        this.def = start;
	    }
	 
	    /**
	    * Set the {@code YamlConfiguration} to use.
	    * @param config The config to set.
	    */
	    public static void setFile(YamlConfiguration config) {
	        LANG = config;
	    }
	 
	    @Override
	    public String toString() {
	        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
	    }
	 
	    /**
	    * Get the default value of the path.
	    * @return The default value of the path.
	    */
	    public String getDefault() {
	        return this.def;
	    }
	 
	    /**
	    * Get the path to the string.
	    * @return The path to the string.
	    */
	    public String getPath() {
	        return this.path;
	    }
	}
	
	/**
	 * Load the lang.yml file.
	 * @return The lang.yml config.
	 */
	public boolean loadLang() {
	    File lang = new File(Main.getPlugin().getDataFolder(), "lang.yml");
	    if (!lang.exists()) {
	        try {
	        	Main.getPlugin().getDataFolder().mkdir();
	            lang.createNewFile();
	            InputStream defConfigStream = Main.getPlugin().getResource("lang.yml");
	            if (defConfigStream != null) {
	                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	                defConfig.save(lang);
	                Msgs.setFile(defConfig);
	            }
	        } 
	        catch(IOException e) {
	            new ReportError(e); // So they notice
	            Util.cinfo("&cCouldn't create language file.");
	            Util.cinfo("&cThis is a fatal error. Now disabling");
	            return false; // Without it loaded, we can't send them messages
	        }
	    }
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
	    for(Msgs item:Msgs.values()) {
	        if (conf.getString(item.getPath()) == null) {
	            conf.set(item.getPath(), item.getDefault());
	        }
	    }
	    Msgs.setFile(conf);
	    Lang.LANG_YML = conf;
	    Lang.LANG_FILE = lang;
	    try {
	        conf.save(getLangFile());
	    } catch(IOException e) {
	    	Util.cinfo("&cFailed to save lang.yml.");
	        new ReportError(e);
	        return false;
	    }
		return true;
	}
	
	/**
	* Gets the lang.yml config.
	* @return The lang.yml config.
	*/
	public YamlConfiguration getLang() {
	    return LANG_YML;
	}
	 
	/**
	* Get the lang.yml file.
	* @return The lang.yml file.
	*/
	public File getLangFile() {
	    return LANG_FILE;
	}
	
}
