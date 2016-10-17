package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.PlayerInventoryClone;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class SpawnCorpse implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		CorpseData data;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can run this command. Sorry about that.");
			return true;
		}
		if (!sender.hasPermission("corpses.spawn")) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have enough permissions!");
			return true;
		}
		if (args.length == 0) {
			Player p = (Player) sender;
			
			data = Main.getPlugin().corpses.spawnCorpse(p, makeInv(p)).setSelectedSlot(p.getInventory().getHeldItemSlot());
			p.sendMessage(ChatColor.GREEN + "Corpse of yourself spawned!");
			Util.callEvent(new CorpseSpawnEvent(data, true));
		} else if (args.length == 1) {
			Player p = Bukkit.getServer().getPlayer(args[0]);
			if (p == null) {
				sender.sendMessage(ChatColor.RED + "Player " + args[0]
						+ " is not online!");
				return true;
			}

			data = Main.getPlugin().corpses.spawnCorpse(p, makeInv(p)).setSelectedSlot(p.getInventory().getHeldItemSlot());
			sender.sendMessage(ChatColor.GREEN + "Spawned corpse of "
					+ p.getName() + "!");
			Util.callEvent(new CorpseSpawnEvent(data, true));
		} else {
			sender.sendMessage(ChatColor.RED + "Correct Usage: /"
					+ commandLabel + " [Player]");
		}
		return true;
	}
	
	Inventory makeInv(Player p){
		String ver = Main.getPlugin().getServerVersion();
		PlayerInventoryClone inv = new PlayerInventoryClone(p);
		if(!ver.startsWith("v1_8")){
			inv.setOffHand(p.getInventory().getItemInOffHand());
		}
		return inv.toInventory();
	}
}
