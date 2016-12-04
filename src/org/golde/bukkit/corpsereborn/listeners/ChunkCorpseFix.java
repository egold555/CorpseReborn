package org.golde.bukkit.corpsereborn.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class ChunkCorpseFix implements Listener{

	@EventHandler
	public void chunkLoad(ChunkLoadEvent e){
		List<World> world = ConfigData.getWorld();
		if(world == null){
			world = Bukkit.getWorlds();
		}
		if(world.contains(e.getWorld())){
			for(final CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
				if(Util.isCorpseInChunk(e.getChunk(), cd)){
					//delay corpse cause the chunk was not loaded
					new BukkitRunnable(){
						public void run(){
							cd.resendCorpseToEveryone();
						}
					}.runTaskLater(Main.getPlugin(), 2);
					
				}
			}
		}
	}
	
}
