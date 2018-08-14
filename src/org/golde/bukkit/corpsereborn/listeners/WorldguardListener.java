package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldguardListener implements Listener{ //TODO: Fix the worldguard bugs :|

	private StateFlag corpseSpawn = new StateFlag("corpse-spawn", true);
	private StateFlag corpseClick = new StateFlag("corpse-click", true);
	private WorldGuardPlugin worldGuard;
	
	public WorldguardListener(WorldGuardPlugin worldGuard) {
		this.worldGuard = worldGuard;
		worldGuard.getFlagRegistry().register(corpseSpawn);
		worldGuard.getFlagRegistry().register(corpseClick);
	}
	
	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, Main.getPlugin());
	}
	
	@EventHandler
	public void onCorpseSpawn(CorpseSpawnEvent e) {
			if(!getFlagStatus(corpseSpawn, e.getCorpse().getOrigLocation())) {
				e.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onCorpseClick(CorpseClickEvent e) {
		if(!getFlagStatus(corpseClick, e.getCorpse().getOrigLocation())) {
			e.setCancelled(true);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCowSpawn(final EntitySpawnEvent e) {
		if(e.getEntityType().equals(EntityType.COW)) {
			Location loc = e.getLocation();
			if(!getFlagStatus(DefaultFlag.MOB_SPAWNING,loc)) {
				e.setCancelled(false);
				new BukkitRunnable() {
					@Override
					public void run() {
						if(!Main.getPlugin().corpses.isValidCow((LivingEntity)e.getEntity())) {
							e.getEntity().remove();
						}
					}
				};
			}
		}
	}
	
	private boolean getFlagStatus(StateFlag flag, Location loc) {
		RegionContainer container = worldGuard.getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(loc, (LocalPlayer)null, flag);
	}
	
}
