package org.golde.bukkit.corpsereborn;

import java.util.ArrayList;
import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum ServerType {
	BUKKIT(true), 
	SPIGOT(true), 
	PAPER_SPIGOT(true), 
	TACO_SPIGOT(false),
	GLOWSTONE(false), 
	SPONGE(false),
	CAULDRON(false),
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
		
		if(Bukkit.getVersion().toLowerCase().contains("taco")){
			return TACO_SPIGOT;
		}
		
		try{
			Class.forName("com.destroystokyo.paper.PaperConfig");
			return PAPER_SPIGOT;
		}catch (Exception e){}
		
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

