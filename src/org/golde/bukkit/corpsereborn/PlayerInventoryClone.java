package org.golde.bukkit.corpsereborn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryClone {
	
	private Inventory i;
	
	public PlayerInventoryClone(Player p){
		PlayerInventory pi = p.getInventory();
		i = Bukkit.getServer().createInventory(null, 54);
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
	
	public Inventory toInventory(){
		return i;
	}
	
	public PlayerInventoryClone setOffHand(ItemStack stack){
		i.setItem(7, stack);
		return this;
	}

}
