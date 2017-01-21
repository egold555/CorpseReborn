package org.golde.bukkit.corpsereborn;

import java.util.ArrayList;
import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum ServerType {
	BUKKIT(true), 
	SPIGOT(true), 
	PAPER_SPIGOT(false), //corpse just gets removed. No errors.
	TACO_SPIGOT(false), //fork of paper
	GLOWSTONE(false), //Plugin does not load
	SPONGE(false), //Not tested
	CAULDRON(false), //NMS Does not load. 
	UNKNOWN(false);

	private final boolean compatible;

	ServerType(boolean compatible){
		this.compatible = compatible;
	}
	
	public boolean isCompatible(){
		return compatible;
	}
	
	public static String getSupportedVersions(){
		ArrayList<ServerType> servertype = new ArrayList<ServerType>(EnumSet.allOf(ServerType.class));
		ArrayList<String> com = new ArrayList<String>();
		for(ServerType st:servertype){if(st.isCompatible()){com.add(st.name());}}
		return Util.commaSep(com);
	}

	public static ServerType whatAmI(JavaPlugin p){
		
		if(Bukkit.getVersion().toLowerCase().contains("paper")){
			return PAPER_SPIGOT;
		}else if(Bukkit.getVersion().toLowerCase().contains("taco")){
			return TACO_SPIGOT;
		}
		
		/*try{
			Class.forName("org.github.paperspigot.PaperSpigotConfig");
			return PAPER_SPIGOT;
		}catch (Exception e){}*/
		
		try{
			Class.forName("net.glowstone.GlowServer");
			return GLOWSTONE;
		}catch (Exception e){}

		try{
			Class.forName("org.spongepowered.server.SpongeVanilla");
			return SPONGE;
		}catch (Exception e){}
		
		try{
			Class.forName("net.minecraftforge.cauldron.CauldronUtils");
			return CAULDRON;
		}catch (Exception e){}
		
		try{
			Class.forName("org.spigotmc.SpigotConfig");
			return SPIGOT;
		}catch (Exception e){}
		
		try{
			Class.forName("org.bukkit.Bukkit");
			return BUKKIT;
		}catch (Exception e){}
		
		
		return UNKNOWN;
	}
}

