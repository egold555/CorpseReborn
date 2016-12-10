package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class ChunkCorpseFix implements Listener{
	
	/*
	@EventHandler
	public void chunkLoad(ChunkLoadEvent e){
		//Bukkit.broadcastMessage(ChatColor.AQUA + "Event: ChunkLoadEvent");
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
					}.runTaskLater(Main.getPlugin(), 5);

				}
			}
		}
	}
	// We used this to fix a problem with teleported, but fixed it in a better way
	// in NMSCorpses_v1_11_R1 in the function tick(). Added delayed execution of sendCorpseToPlayer.
	*/
	
	
	@EventHandler
	public void respawn(PlayerRespawnEvent e){
		for(final CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			cd.setCanSee(e.getPlayer(), false);
		}
	}

}
