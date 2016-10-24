package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.ServerVersion;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;

public class SlimeHit implements Listener{

	@EventHandler(priority=EventPriority.LOWEST)
	public void leftClick(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK){
			if(handle((Player)e.getDamager(), e.getEntity(), TypeOfClick.LEFT_CLICK)){
				e.setCancelled(true);
			}
		}
		if(e.getDamager() instanceof Slime && e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK ){
			if(Main.getPlugin().corpses.isValidSlime((Slime)e.getDamager())){
				e.setDamage(0);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled = true)
	public void rightClick(PlayerInteractAtEntityEvent e){
		if(Main.serverVersion.getNiceVersion().compareTo(ServerVersion.v1_10 ) < 0 || e.getHand().equals(EquipmentSlot.HAND)){
			if(handle(e.getPlayer(), e.getRightClicked(), TypeOfClick.RIGHT_CLICK)){
				e.setCancelled(true);
			}
		}	
	}
	
	boolean handle(Player p, Entity entity, TypeOfClick clickType){
		if(entity instanceof Slime){
			return Main.getPlugin().corpses.slimeHit(p, (Slime)entity, clickType);
		}
		return false;
	}
	
}
