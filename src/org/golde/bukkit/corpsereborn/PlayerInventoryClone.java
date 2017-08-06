package org.golde.bukkit.corpsereborn;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryClone {
	
	private static Inventory i;
	
	public PlayerInventoryClone(Player p){
		PlayerInventory pi = p.getInventory();
		i = Bukkit.getServer().createInventory(null, 54, ConfigData.getInventoryName(p));
	
		int temp;
		ArrayList<ItemStack> tempItemStack = new ArrayList<ItemStack>();
		
		//set armor
		temp = 4;
		for(ItemStack stack:pi.getArmorContents()){
			i.setItem(temp, stack);
			temp--;
		}
		
		//set hotbar
		for(int x = 0; x <= 8; x++){
			tempItemStack.add(pi.getItem(x));
		}
		
		temp = 0;
		for(int x = 45; x <= 53; x++){
			i.setItem(x, tempItemStack.get(temp));
			temp++;
		}
		
		tempItemStack.clear();
		
		//set inventory
		for(int x = 9; x <= 35; x++){
			tempItemStack.add(pi.getItem(x));
		}
		
		temp = 0;
		for(int x = 18; x <= 44; x++){
			i.setItem(x, tempItemStack.get(temp));
			temp++;
		}
		
	}
	
	public PlayerInventoryClone(Player p, List<ItemStack> drops){
		ArrayList<ItemStack> itemsToPlace = new ArrayList<ItemStack>(drops);  // clone list.
		
		PlayerInventory pi = p.getInventory();
		i = Bukkit.getServer().createInventory(null, 54, ConfigData.getInventoryName(p));
	
		int temp;
		ArrayList<ItemStack> tempItemStack = new ArrayList<ItemStack>();
		
		//set armor
		temp = 4;
		for(ItemStack stack:pi.getArmorContents()){
			if (stack != null && itemsToPlace.contains(stack)) {
				i.setItem(temp, stack);
				itemsToPlace.remove(stack);
			}

			temp--;
		}
		
		if(Main.serverVersion.getNiceVersion().compareTo(ServerVersion.v1_9 ) >= 0){
			// handle off hand.
			ItemStack stack = pi.getItemInOffHand();
			if (stack != null && itemsToPlace.contains(stack)) {
				i.setItem(7, stack);
				itemsToPlace.remove(stack);
			}
		}
		
		//set hotbar
		for(int x = 0; x <= 8; x++){
			ItemStack stack = pi.getItem(x);
			if (stack != null && itemsToPlace.contains(stack)) {
				tempItemStack.add(pi.getItem(x));
				itemsToPlace.remove(stack);
			}
		}
		
		temp = 0;
		for(int x = 45; x <= 53; x++){
			if (temp >= tempItemStack.size())
				break;
			i.setItem(x, tempItemStack.get(temp));
			temp++;
		}
		
		tempItemStack.clear();
		
		//set rest of items.

		temp = 0;
		for(int x = 18; x <= 44; x++){
			if (temp >= itemsToPlace.size())
				break;
			i.setItem(x, itemsToPlace.get(temp));
			temp++;
		}
		
	}
	
	public PlayerInventoryClone(Player p, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, ItemStack offHand){
		i = Bukkit.getServer().createInventory(null, 54, ConfigData.getInventoryName(p));
	
		int temp;
		
		if (helmet != null)
			i.setItem(1,  helmet);
		if (chestplate != null)
			i.setItem(2, chestplate);
		if (leggings != null)
			i.setItem(3,  leggings);
		if (boots != null)
			i.setItem(4,  boots);
		if (offHand != null)
			i.setItem(7, offHand);
		
		temp = 18;
		if (mainInventory != null) {
			for (ItemStack is: mainInventory) {
				if (is != null)
				    i.setItem(temp, is);
				temp += 1;
				if (temp >= 54)
					break;
			}
		}
		
		if (hand != null)
			i.setItem(53, hand);

	}
	
	public Inventory toInventory(){
		return i;
	}
	
	public PlayerInventoryClone setOffHand(ItemStack stack){
		i.setItem(7, stack);
		return this;
	}
	
	public static ItemStack getHelmet(){
		return i.getItem(1);
	}
	
	public static ItemStack getChestplate(){
		return i.getItem(2);
	}
	
	public static ItemStack getLeggings(){
		return i.getItem(3);
	}
	
	public static ItemStack getBoots(){
		return i.getItem(4);
	}

}
