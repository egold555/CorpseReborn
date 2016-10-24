package org.golde.bukkit.corpsereborn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;


public class Util {
	private static String prefix = "[CorpseReborn] ";

	public static int getNearestMultipleOfNumberCeil(int number, int multiple) {
		return (int) (multiple * Math.ceil((double) number / multiple));
	}

	public static void info(String text){
		Bukkit.getServer().getLogger().info(prefix + text);
	}

	public static void warning(String text){
		Bukkit.getServer().getLogger().warning(prefix + text);
	}

	public static void severe(String text){
		Bukkit.getServer().getLogger().severe(prefix + text);
	}
	
	public static void cinfo(String text){
		text = text.replaceAll("&", "§");
		Bukkit.getConsoleSender().sendMessage(prefix + text);
	}

	public static boolean playerInCorrectWorld(Player p){
		if(ConfigData.getWorld() == null){
			return true;
		}
		for(World world:ConfigData.getWorld()){
			if(p.getWorld() == world){
				return true;
			}
		}
		return false;
	}

	public static boolean isWithinRadius(Player p, double radius, Location corpseLocation){
		Location playerLoc = p.getLocation();
		if(playerLoc.getWorld() != corpseLocation.getWorld()){
			return false;
		}
		double dis = Math.sqrt((corpseLocation.getX()-playerLoc.getX()) * (corpseLocation.getX()-playerLoc.getX()) + (corpseLocation.getZ()-playerLoc.getZ()) * (corpseLocation.getZ()-playerLoc.getZ()));
		if(dis <= radius){return true;}
		return false;
	}

	public static ArrayList<CorpseData> removeCorpsesInRadius(Player p, double radius){
		ArrayList<CorpseData> returnCorpses = new ArrayList<CorpseData>();
		ArrayList<CorpseData> iWantToRemove = new ArrayList<CorpseData>();
		for(CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			Location l = cd.getOrigLocation();
			if(isWithinRadius(p, radius, l)){
				iWantToRemove.add(cd);
			}
		}

		for(CorpseData cd:iWantToRemove){
			returnCorpses.add(cd);
			Main.getPlugin().corpses.removeCorpse(cd);
		}

		return returnCorpses;
	}

	public static ArrayList<CorpseData> removeAllCorpses(Player p){
		ArrayList<CorpseData> returnCorpses = new ArrayList<CorpseData>();
		ArrayList<CorpseData> iWantToRemove = new ArrayList<CorpseData>();
		for(CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			iWantToRemove.add(cd);
		}

		for(CorpseData cd:iWantToRemove){
			returnCorpses.add(cd);
			Main.getPlugin().corpses.removeCorpse(cd);
		}

		return returnCorpses;
	}


	public static boolean isInventoryEmpty(Inventory i){
		for(ItemStack item : i.getContents())
		{
		    if(item != null)
		      return false;
		}
		return true;
	}
	
	public static CorpseData isCorpseInventory(InventoryView iv){
		for(CorpseData cd:Main.getPlugin().corpses.getAllCorpses()){
			if(cd != null && iv != null && cd.getInventoryView() != null && cd.getInventoryView().equals(iv)){
				return cd;
			}
		}
		return null;
	}

	public static void removeCorpse(CorpseData cd){
		Main.getPlugin().corpses.removeCorpse(cd);
	}
	
	public static void callEvent(Event event){
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
	
	public static Inventory makeNiceInv(Player p){
		PlayerInventoryClone inv = new PlayerInventoryClone(p);
		if(Main.serverVersion.getNiceVersion().compareTo(ServerVersion.v1_9 ) >= 0){
			inv.setOffHand(p.getInventory().getItemInOffHand());
		}
		return inv.toInventory();
	}
	
	public static Inventory makeNiceInv(Player p, ItemStack[] mainInventory, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand, ItemStack offHand){
		PlayerInventoryClone inv = new PlayerInventoryClone(p, mainInventory, helmet, chestplate, leggings, boots, hand, offHand);

		return inv.toInventory();
	}

}