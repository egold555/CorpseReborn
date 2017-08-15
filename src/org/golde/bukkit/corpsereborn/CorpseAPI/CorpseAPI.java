package org.golde.bukkit.corpsereborn.CorpseAPI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 * This class is a API to allow developers to spawn and remove corpses.
 * @author Eric Golde
 *
 */
public class CorpseAPI {
	
	public static final int ROTATION_SOUTH = 0;
	public static final int ROTATION_WEST = 1;
	public static final int ROTATION_NORTH = 2;
	public static final int ROTATION_EAST = 3;
	
	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param overrideName A username to give the corpse. Set to null to use the player name with the option in the config to customize it.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @param helmet Display item in the helmet slot of the corpse.
	 * @param chestplate Display item in the chestplate slot of the corpse.
	 * @param leggings Display item in the leggings slot of the corpse.
	 * @param boots Display item in the boots slot of the corpse.
	 * @param hand Display item in the hand slot of the corpse.
	 * @param offHand Display item in the offhand slot of the corpse.
	 * @param rotation Sets the rotation of the corpse. 0: South, 1:West, 2:North, 3:East
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, String overrideName, Location location, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, ItemStack offHand, int rotation)
	{
		CorpseData data = null;
		try{
			data = Main.getPlugin().corpses.spawnCorpse(player, overrideName, location, Util.makeNiceInv(player, mainInventory, helmet, chestplate, leggings, boots, hand, offHand), rotation);

			if (hand != null)
				data.setSelectedSlot(8); 
			Util.callEvent(new CorpseSpawnEvent(data, true));
		}catch(Exception ex){
			new ReportError(ex);
		}
		return data;
	}
	
	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param overrideName A username to give the corpse. Set to null to use the player name with the option in the config to customize it.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @param helmet Display item in the helmet slot of the corpse.
	 * @param chestplate Display item in the chestplate slot of the corpse.
	 * @param leggings Display item in the leggings slot of the corpse.
	 * @param boots Display item in the boots slot of the corpse.
	 * @param hand Display item in the hand slot of the corpse.
	 * @param offHand Display item in the offhand slot of the corpse.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, String overrideName, Location location, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, ItemStack offHand)
	{
		return spawnCorpse(player, overrideName, location, mainInventory, helmet, chestplate, leggings, boots, hand, offHand, 0);
	}

	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, Location location, ItemStack[] mainInventory)
	{
		return spawnCorpse(player, null, location, mainInventory, null, null, null, null, null, null);
	}

	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @param helmet Display item in the helmet slot of the corpse.
	 * @param chestplate Display item in the chestplate slot of the corpse.
	 * @param leggings Display item in the leggings slot of the corpse.
	 * @param boots Display item in the boots slot of the corpse.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, Location location, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots)
	{
		return spawnCorpse(player, null, location, mainInventory, helmet, chestplate, leggings, boots, null, null);
	}

	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @param helmet Display item in the helmet slot of the corpse.
	 * @param chestplate Display item in the chestplate slot of the corpse.
	 * @param leggings Display item in the leggings slot of the corpse.
	 * @param boots Display item in the boots slot of the corpse.
	 * @param hand Display item in the hand slot of the corpse.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, Location location, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand)
	{
		return spawnCorpse(player, null, location, mainInventory, helmet, chestplate, leggings, boots, hand, null);
	}

	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param overrideName A username to give the corpse. Set to null to use the player name with the option in the config to customize it.
	 * @param location Location to spawn the corpse at.
	 * @param mainInventory Items to put in the inventory-to-be-looted.
	 * @param helmet Display item in the helmet slot of the corpse.
	 * @param chestplate Display item in the chestplate slot of the corpse.
	 * @param leggings Display item in the leggings slot of the corpse.
	 * @param boots Display item in the boots slot of the corpse.
	 * @param hand Display item in the hand slot of the corpse.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, String overrideName, Location location, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand)
	{
		return spawnCorpse(player, overrideName, location, mainInventory, helmet, chestplate, leggings, boots, hand, null);
	}

	/**
	 * Spawns a corpse at the given location
	 * @param player Player to spawn the corpse of. This must be a online player.
	 * @param location Location to spawn the corpse at.
	 * @return Returns the data for the corpse. This can be used to delete the specific corpse. 
	 */
	public static CorpseData spawnCorpse(Player player, Location location){
		return spawnCorpse(player, null, location, null, null, null, null, null, null, null);
	}


	/**
	 * Remove a corpse from the given CorpseData
	 * @param data The corpse to remove
	 */
	public static void removeCorpse(CorpseData data){
		try{
			Main.getPlugin().corpses.removeCorpse(data);
		}catch(Exception ex){
			new ReportError(ex);
		}
	}

	/**
	 * Remove all corpses in a certain world
	 * @param world The world to remove all the corpses in
	 */
	public static void removeAllCorpses(World world){
		Util.removeAllCorpses(world);
	}

	/**
	 * Removes all corpses in ALL worlds
	 */
	public static void removeAllCorpses(){
		for(World world:ConfigData.getWorld()){
			removeAllCorpses(world);
		}
	}
	
	/**
	 * Get all corpses in a certain radius
	 * @param loc Location to find corpse from
	 * @param radius in which to look for corpses
	 * @return List of all corpses found
	 */
	public static List<CorpseData> getCorpseInRadius(Location loc, int radius) {
		List<CorpseData> toReturn = new ArrayList<CorpseData>();
		for(CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			Location l = cd.getOrigLocation();
			if(Util.isWithinRadius(loc, radius, l)){
				toReturn.add(cd);
			}
		}
		return toReturn;
	}

}
