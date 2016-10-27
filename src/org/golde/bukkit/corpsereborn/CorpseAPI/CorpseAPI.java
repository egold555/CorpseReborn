package org.golde.bukkit.corpsereborn.CorpseAPI;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 * This class is a API to allow developers to spawn and remove corpses.
 * @author Eric Golde
 *
 */
public class CorpseAPI {
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
		CorpseData data = Main.getPlugin().corpses.spawnCorpse(player, overrideName, location, Util.makeNiceInv(player, mainInventory, helmet, chestplate, leggings, boots, hand, offHand));
		
		if (hand != null)
			data.setSelectedSlot(8); 
		Util.callEvent(new CorpseSpawnEvent(data, true));
		return data;
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
	 * 
	 * @param data remove a corpse from the given CorpseData
	 */
	public static void removeCorpse(CorpseData data){
		Main.getPlugin().corpses.removeCorpse(data);
	}
	
	/**
	 * 
	 * @param world The world to remove all the corpses in
	 */
	public void removeAllCorpses(World world){
		Util.removeAllCorpses(world);
	}
	
	/**
	 * Removes all corpses in ALL worlds
	 */
	public void removeAllCorpses(){
		for(World world:ConfigData.getWorld()){
			removeAllCorpses(world);
		}
	}
	
}
