package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.ServerVersion;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;
import org.golde.bukkit.corpsereborn.nms.NmsBase;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;

public class CowHit implements Listener{

	@EventHandler(priority=EventPriority.LOWEST)
	public void leftClick(EntityDamageByEntityEvent e){
		try{
			if(e.getDamager() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK){
				if(handle((Player)e.getDamager(), e.getEntity(), TypeOfClick.LEFT_CLICK)){
					e.setCancelled(true);
				}
			}
			if(e.getDamager().getType() == NmsBase.ENTITY && e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK ){
				if(Main.getPlugin().corpses.isValidCow((LivingEntity)e.getDamager())){
					e.setDamage(0);
					e.setCancelled(true);
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void rightClick(PlayerInteractAtEntityEvent e){
		try{
			if(Main.serverVersion.getNiceVersion().compareTo(ServerVersion.v1_10 ) < 0 || e.getHand().equals(EquipmentSlot.HAND)){
				if(handle(e.getPlayer(), e.getRightClicked(), TypeOfClick.RIGHT_CLICK)){
					e.setCancelled(true);
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	boolean handle(Player p, Entity entity, TypeOfClick clickType){
		if(entity.getType() == NmsBase.ENTITY){
			CorpseData cd = Util.getCorpseInRaduis(entity.getLocation(), 2);
			if(cd == null) {
				return false;
			}
			return Main.getPlugin().corpses.cowHit(p, cd, clickType);
		}
		return false;
	}
	
	@EventHandler
	public void click(PlayerInteractEvent e) {
		if(e.getAction() != Action.PHYSICAL) {
			if(e.getClickedBlock() != null) {
				Location loc = e.getClickedBlock().getLocation();
				CorpseData cd = Util.getCorpseInRaduis(loc, 2);
				if(cd != null) {
					//Bukkit.broadcastMessage(ChatColor.RED + "CD: " + ChatColor.BLUE + cd.toString());
					Main.getPlugin().corpses.cowHit(e.getPlayer(), cd, TypeOfClick.UNKNOWN);
				}
				
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH) //TODO: Handel /kill @e command
	public void cowDamageEvent(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if(entity == null || entity.getType() != NmsBase.ENTITY) {
			return;
		}
		if(entity.getCustomName() != null && entity.getCustomName().equals("CRHitbox")) { //TODO: Check name of cow, option to log remove bugged stuff (Global method for this)
			e.setDamage(0);
		}
	}

}
