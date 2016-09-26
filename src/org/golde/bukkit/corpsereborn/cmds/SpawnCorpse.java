package org.golde.bukkit.corpsereborn.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;

public class SpawnCorpse implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "You cannot run this command at this time!");
			return true;
		}
		if (!sender.hasPermission("corpses.spawn")) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have enough permissions!");
			return true;
		}
		if (args.length == 0) {
			Player p = (Player) sender;
			Inventory items = null;
			if (ConfigData.hasLootingInventory()) {
				items = Bukkit.getServer().createInventory(null, 54,
						p.getName() + "'s Items");
				for (ItemStack is : p.getInventory().getContents()) {
					if (is != null) {
						items.addItem(is);
					}
				}
				for (ItemStack is : p.getInventory().getArmorContents()) {
					if (is != null) {
						items.addItem(is);
					}
				}
			}
			Main.getPlugin().corpses.spawnCorpse(p, items);
			p.sendMessage(ChatColor.RED + "Corpse of yourself spawned!");
		} else if (args.length == 1) {
			Player p = Bukkit.getServer().getPlayer(args[0]);
			if (p == null) {
				sender.sendMessage(ChatColor.RED + "Player " + args[0]
						+ " is not online!");
				return true;
			}
			Inventory items = null;
			if (ConfigData.hasLootingInventory()) {
				items = Bukkit.getServer().createInventory(null, 54,
						p.getName() + "'s Items");
				for (ItemStack is : p.getInventory().getContents()) {
					if (is != null) {
						items.addItem(is);
					}
				}
				for (ItemStack is : p.getInventory().getArmorContents()) {
					if (is != null) {
						items.addItem(is);
					}
				}
			}
			Main.getPlugin().corpses.spawnCorpse(p, items);
			sender.sendMessage(ChatColor.GREEN + "Spawned corpse of "
					+ p.getName() + "!");
		} else {
			sender.sendMessage(ChatColor.RED + "Correct Usage: /"
					+ commandLabel + " [Player]");
		}
		return true;
	}
}
