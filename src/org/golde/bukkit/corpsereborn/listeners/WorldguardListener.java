package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseRemoveEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldguardListener implements Listener{

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
	
	private boolean getFlagStatus(StateFlag flag, Location loc) {
		RegionContainer container = worldGuard.getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(loc, (LocalPlayer)null, flag);
	}
	
}
