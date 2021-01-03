package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.potion.PotionEffectType;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.dump.ReportError;
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
		try{
			for(final CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
				cd.setCanSee(e.getPlayer(), false);
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}
	
	@EventHandler
	public void onWorldLoaded(WorldLoadEvent event){
		for(Entity e:event.getWorld().getEntities()){
			if(e instanceof LivingEntity && e instanceof Cow /*&& e.getType() == NmsBase.ENTITY*/){ //TODO: Check name of cow, option to log remove bugged stuff (Global method for this)
				LivingEntity le = (LivingEntity)e;
				if(le.hasPotionEffect(PotionEffectType.INVISIBILITY)){
					Util.info("Removed bugged cow at " + e.getLocation().getBlockX() + " " + e.getLocation().getBlockY() + " " + e.getLocation().getBlockZ());
					e.remove();
				}
			}
		}
	}

}
