package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.PlayerInventoryClone;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerDeath implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		try{
			
			if(ConfigData.getDamageCausesThatDontCauseACorpse().contains(e.getEntity().getLastDamageCause().getCause())) {
				return;
			}
			
			if (ConfigData.isOnDeath() && Util.playerInCorrectWorld(e.getEntity())) {
				CorpseData data;
				PlayerInventoryClone inv = new PlayerInventoryClone(e.getEntity(), e.getDrops());
				
				int facing = yawToFacing(e.getEntity().getLocation().getYaw());
				
				data = Main.getPlugin().corpses.spawnCorpse(e.getEntity(), null, offsetLocationFacing(e.getEntity().getLocation(), facing), inv.toInventory(), facing).setSelectedSlot(e.getEntity().getInventory().getHeldItemSlot());
				
				CorpseSpawnEvent cse = new CorpseSpawnEvent(data, false);
				Util.callEvent(cse);
				if(cse.isCancelled()){
					Main.getPlugin().corpses.removeCorpse(data);
				}else{
					if (ConfigData.hasLootingInventory()) {		
						e.getDrops().clear();
					}
				}
			}
			
			// For each corpse, remove player from view of that corpse.
			//in therory fixing a bug. Realy not sure
			for (CorpseData cd:Main.getPlugin().corpses.getAllCorpses()) {
				cd.removeFromMap(e.getEntity());
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}
	
	private int yawToFacing(float yaw) {
		int facing = CorpseAPI.ROTATION_NORTH;
		
		if(yaw >= -45 && yaw <= 45) {
			facing = CorpseAPI.ROTATION_SOUTH;
		} 
		else if(yaw >= 45 && yaw <=135) {
			facing = CorpseAPI.ROTATION_WEST;
		}
		else if(yaw <= -45 && yaw >=-135) {
			facing = CorpseAPI.ROTATION_EAST;
		}
		else if(yaw <= -135 && yaw >=-225) {
			facing = CorpseAPI.ROTATION_NORTH;
		}
		else if(yaw <= -225 && yaw >=-315) {
			facing = CorpseAPI.ROTATION_WEST;
		}
		else if(yaw >= 135 && yaw <= 225) {
			facing = CorpseAPI.ROTATION_NORTH;
		}
		else if(yaw >= 225 && yaw <= 315) {
			facing = CorpseAPI.ROTATION_EAST;
		}
		else if (yaw >= 315) {
			facing = CorpseAPI.ROTATION_SOUTH;
		}
		else if (yaw <= -315) {
			facing = CorpseAPI.ROTATION_SOUTH;
		}
		
		return facing;
	}
	
	private final float offset = 2f;
	
	//NON CLIPABLE LOCATION?!?!?
	private Location offsetLocationFacing(Location loc, int facing) { //TODO: Fix
		
		Location newLoc = loc.clone();
		if(Main.getPlugin().isDev) {
			Bukkit.broadcastMessage("LOC: " + loc);
		}
		newLoc = newLoc.add(0, 0, offset);

		if(Main.getPlugin().isDev) {
			Bukkit.broadcastMessage("NEW: " + newLoc);
		}
//		if(facing == CorpseAPI.ROTATION_SOUTH) {
//			newLoc = newLoc.add(0, 0, offset);
//		}
//		else if(facing == CorpseAPI.ROTATION_EAST) {
//			newLoc = newLoc.add(offset, 0, 0);
//		}
//		else if(facing == CorpseAPI.ROTATION_NORTH) {
//			newLoc = newLoc.subtract(0, 0, offset);
//		}
//		else if(facing == CorpseAPI.ROTATION_WEST) {
//			newLoc = newLoc.subtract(offset, 0, 0);
//		}
		
		return newLoc;
	}
}
