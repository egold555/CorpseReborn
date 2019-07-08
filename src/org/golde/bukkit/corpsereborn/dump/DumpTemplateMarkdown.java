package org.golde.bukkit.corpsereborn.dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import net.steppschuh.markdowngenerator.text.heading.Heading;

public class DumpTemplateMarkdown {

	private final String exception;
	private final boolean isFromDumpCommand;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	public DumpTemplateMarkdown(Exception ex){
		if(ex instanceof DumpException){
			exception = "(Dump Command)";
			isFromDumpCommand = true;
			return;
		}
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		exception = w.toString();
		isFromDumpCommand = false;
	}
	
	private Table getPlugins() {
		
		Table.Builder builder = new Table.Builder();
		builder.withAlignments(Table.ALIGN_LEFT);
		builder.addRow("Name", "Version", "Author");
		for(Plugin pl : Bukkit.getPluginManager().getPlugins()){
			builder.addRow(pl.getName(), pl.getDescription().getVersion(), String.join(", ", pl.getDescription().getAuthors()));
		}
		
		return builder.build();
	}
	
	private CodeBlock getCommonInfo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Date: " + dateFormat.format(new Date()));
		sb.append("CorpseReborn Version: " + Main.getPlugin().getDescription().getVersion().split(" ")[0]);
		sb.append("");
		sb.append("Server Version: " + Bukkit.getVersion());
		sb.append("Server Type: " + Main.serverType.name());
		sb.append("NMS Version: " + Main.serverVersion.name());
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getConfig() {
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(Main.getPlugin().getConfig().getCurrentPath()));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		}
		catch(Exception e) {
			
		}
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getCorpseData() {
		StringBuilder sb = new StringBuilder();
		
		int tempCounter = 0;
		for(CorpseData d:Main.getPlugin().corpses.getAllCorpses()) {
			String first = tempCounter + ".";

			sb.append(first + "location: " + d.getOrigLocation());
			sb.append(first + "name: " +  d.getCorpseName());
			if(d.getProfilePropertiesJson() != null) {
				sb.append(first + "skin: " + d.getProfilePropertiesJson());
			}
			sb.append(first + "rotation: " + d.getRotation());
			sb.append(first + "slot: " + d.getSelectedSlot());
			Inventory inv = d.getLootInventory();

			for(int slot = 0; slot < inv.getSize(); slot++) {
				ItemStack item = inv.getItem(slot);
				if(item != null) {
					sb.append(first + "inventory." + slot + ": " + item);
				}
			}

			tempCounter++;
		}
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getException() {
		StringBuilder sb = new StringBuilder();
		sb.append(exception);
		
		return new CodeBlock(sb.toString());
	}
	
	public String getOutput(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(new Heading("Common Info: ", 1));
		sb.append(getCommonInfo());
		
		sb.append(new Heading("Exception: ", 1));
		sb.append(getException());
		
		sb.append(new Heading("Plugins: ", 1));
		sb.append(getPlugins());
		
		sb.append(new Heading("Config: ", 1));
		sb.append(getConfig());
		
		sb.append(new Heading("Corpse Data: ", 1));
		sb.append(getCorpseData());
		
		return sb.toString();
	}
	
}
