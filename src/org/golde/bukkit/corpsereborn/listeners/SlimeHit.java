package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.golde.bukkit.corpsereborn.Main;

public class SlimeHit implements Listener{

	@EventHandler
	public void leftClick(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK){
			if(handle((Player)e.getDamager(), e.getEntity())){
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
	
	@EventHandler
	public void rightClick(PlayerInteractAtEntityEvent e){
		if(handle(e.getPlayer(), e.getRightClicked())){
			e.setCancelled(true);
		}	
	}
	
	boolean handle(Player p, Entity entity){
		if(entity instanceof Slime){
			return Main.getPlugin().corpses.slimeHit(p, (Slime)entity);
		}
		return false;
	}
	
}
