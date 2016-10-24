package org.golde.bukkit.corpsereborn.nms;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseRemoveEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public abstract class NmsBase {
	protected HashMap<Slime, CorpseData> allSlimes = new HashMap<Slime, CorpseData>();

	public void spawnSlimeForCorpse(CorpseData corpseData)
	{
		if(!ConfigData.hasLootingInventory() || !ConfigData.getNewHitbox()){
			return;
		}
		Location l = corpseData.getOrigLocation();
		l = moveAmount(l);
		allSlimes.put(spawnSlime(l), corpseData);
	}

	public void deleteSlimeForCorpse(CorpseData corpseData)
	{
		Slime slimeToDelete = null;
		for (Slime slime: allSlimes.keySet()) {
			if (corpseData == allSlimes.get(slime)) {
				slimeToDelete = slime;
			}
		}

		if (slimeToDelete != null) {
			allSlimes.remove(slimeToDelete);
			slimeToDelete.remove();
		}

		Util.callEvent(new CorpseRemoveEvent(corpseData, false));
	}

	// Call this when a player hits a slime.
	// returns false is slime is NOT a corpse slime, true if slime is a corpse slime.
	public boolean slimeHit(Player player, Slime slime, TypeOfClick clickType)
	{
		if(isValidSlime(slime)){
			CorpseData data = allSlimes.get(slime);
			Util.callEvent(new CorpseClickEvent(data, player, clickType));
			openInventory(player, data);
			return true;
		}
		return false;
	}


	public boolean isValidSlime(Slime slime){
		CorpseData data = allSlimes.get(slime);
		if (data == null) {
			return false;
		}
		return true;
	}

	private void openInventory(Player player, CorpseData cd)
	{
		InventoryView view = player.openInventory(cd.getLootInventory());
		cd.setInventoryView(view);
	}

	private Slime spawnSlime(Location loc){
		Slime slime = (Slime) loc.getWorld().spawn(loc, Slime.class);
		slime.setSize(4);
		slime.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 100, true));
		slime.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 100, true));
		slime.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 100, true));
		slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 100, true));
		slime.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 100, true));
		addNbtTagsToSlime(slime);
		return slime;
	}

	public void updateSlimes(){
		for (Slime slime: new ArrayList<Slime>(allSlimes.keySet())) {
			CorpseData data = allSlimes.get(slime);
			if(slime.isDead()){
				allSlimes.remove(slime);
				if(data != null){
					spawnSlimeForCorpse(data);
				}
			}else{
				if(data != null){
					teleportSlime(data.getOrigLocation(), slime);
				}
			}
		}
	}

	private Location moveAmount(Location l){
		l = l.clone();
		l = l.add(0, -0.8, -0.9);
		return l;
	}

	private void teleportSlime(Location l, Slime slime){
		l = moveAmount(l);
		if(l.distance(slime.getLocation()) > 0.1f){
			slime.teleport(l);
		}
	}

	public void removeAllSlimes(){
		for (Slime slime: allSlimes.keySet()) {
			slime.remove();
		}
		allSlimes.clear();
	}

	protected abstract void addNbtTagsToSlime(Slime slime);

}
