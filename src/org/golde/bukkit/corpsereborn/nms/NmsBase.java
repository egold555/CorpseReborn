package org.golde.bukkit.corpsereborn.nms;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseRemoveEvent;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public abstract class NmsBase {

	public static final EntityType ENTITY = EntityType.COW;
	@SuppressWarnings("rawtypes")
	public static final Class ENTITY_CLASS = Cow.class;

	protected HashMap<LivingEntity, CorpseData> allSlimes = new HashMap<LivingEntity, CorpseData>();

	public void spawnSlimeForCorpse(CorpseData corpseData)
	{
		try{
			if(!ConfigData.hasLootingInventory() || !ConfigData.getNewHitbox()){
				return;
			}
			Location l = corpseData.getOrigLocation();
			l = moveAmount(l, corpseData.getRotation());
			allSlimes.put(spawnSlime(l), corpseData);
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	public void deleteSlimeForCorpse(CorpseData corpseData)
	{
		try{
			LivingEntity slimeToDelete = null;
			for (LivingEntity slime: allSlimes.keySet()) {
				if (corpseData == allSlimes.get(slime)) {
					slimeToDelete = slime;
				}
			}

			if (slimeToDelete != null) {
				allSlimes.remove(slimeToDelete);
				slimeToDelete.remove();
			}

			Util.callEvent(new CorpseRemoveEvent(corpseData, false));
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	// Call this when a player hits a slime.
	// returns false is slime is NOT a corpse slime, true if slime is a corpse slime.
	public boolean cowHit(Player player, LivingEntity slime, TypeOfClick clickType)
	{
		if(isValidCow(slime)){
			CorpseData data = allSlimes.get(slime);
			CorpseClickEvent cce = new CorpseClickEvent(data, player, clickType);
			Util.callEvent(cce);
			if(!cce.isCancelled()) {
				openInventory(player, data);
				return true;
			}
		}
		return false;
	}


	public boolean isValidCow(LivingEntity slime){
		CorpseData data = allSlimes.get(slime);
		if (data == null) {
			return false;
		}
		return true;
	}

	private void openInventory(Player player, CorpseData cd)
	{
		try{
			InventoryView view = player.openInventory(cd.getLootInventory());
			cd.setInventoryView(view);
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	private LivingEntity spawnSlime(Location loc){
		@SuppressWarnings("unchecked")
		LivingEntity slime = (LivingEntity) loc.getWorld().spawn(loc, ENTITY_CLASS);
		slime.teleport(loc);
		slime.setCustomName("CRHitbox");
		slime.setCustomNameVisible(false);
		try{
			slime.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 100, true));
			slime.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 100, true));
			slime.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 100, true));
			slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 100, true));
			slime.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 100, true));
			slime.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 100, true));
			addNbtTagsToSlime(slime);
		}catch(Exception ex){
			new ReportError(ex);
		}
		return slime;
	}

	public void updateCows(){
		try{
			for (LivingEntity slime: new ArrayList<LivingEntity>(allSlimes.keySet())) {
				CorpseData data = allSlimes.get(slime);
				if(slime.isDead()){
					allSlimes.remove(slime);
					if(data != null){
						spawnSlimeForCorpse(data);
					}
				}else{
					if(data != null){
						teleportSlime(data.getOrigLocation(), slime, data.getRotation());
					}
				}
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	private Location moveAmount(Location l, int rotation){
		l = l.clone();
		if(rotation == 0) {
			l = l.add(0, -0.8, -0.9);
			l.setYaw(0);
		}
		else if(rotation == 1) {
			l = l.add(+0.9, -0.8, 0);
			l.setYaw(90);
		}
		else if(rotation == 2) {
			l = l.add(0, -0.8, +0.9);
			l.setYaw(180);
		}
		else if(rotation == 3) {
			l = l.add(-0.9, -0.8, 0);
			l.setYaw(270);
		}
		return l;
	}

	private void teleportSlime(Location l, LivingEntity slime, int rotation){
		l = moveAmount(l, rotation);
		if(l.distance(slime.getLocation()) > 0.1f){
			slime.teleport(l);
		}
	}

	public void removeAllCows(){
		for (LivingEntity slime: allSlimes.keySet()) {
			slime.remove();
		}
		allSlimes.clear();
	}

	protected abstract void addNbtTagsToSlime(LivingEntity slime);
	
	public Location getNonClippableBlockUnderPlayer(Location loc, int addToYPos) {
		if (loc.getBlockY() < 0) {
			return null;
		}
		for (int y = loc.getBlockY(); y >= 0; y--) {
			Block block = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
			Material m = block.getType();
			if (m.isSolid()) {
				float slabAdjust = 0.0F;
				if (isLowerSlab(block)) {
					slabAdjust = -0.5F;
				}
				return new Location(loc.getWorld(), loc.getX(), y + addToYPos + slabAdjust, loc.getZ());
			}
		}
		
		return new Location(loc.getWorld(), loc.getX(), 1 + addToYPos, loc.getZ());
	}
	
	@SuppressWarnings("deprecation")
	private boolean isLowerSlab(Block block)
	{
		int id = block.getType().getId();
		if (id == 44 || id == 126 || id == 182 || id == 205) {
			int data = block.getData();
			if (data < 8) {
				return true;
			}
		}
		
		return false;
	}
}
